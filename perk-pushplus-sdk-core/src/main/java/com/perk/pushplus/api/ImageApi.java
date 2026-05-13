package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.exception.PushPlusException;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.http.HttpResponse;
import com.perk.pushplus.json.JsonMapper;
import com.perk.pushplus.model.ApiResponse;
import com.perk.pushplus.model.PageQuery;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.image.ImageItem;
import com.perk.pushplus.model.open.image.ImageUploadResult;
import com.perk.pushplus.model.open.image.ImageUploadToken;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 开放接口 - 图片服务（文档「十二. 图片服务接口」）。
 *
 * <p>包含 4 个接口：</p>
 * <ol>
 *   <li>{@link #getUploadToken()} 获取上传凭证</li>
 *   <li>{@link #upload(byte[], String, String) upload} 上传图片到七牛云（form-data，不带 access-key）</li>
 *   <li>{@link #list(PageQuery)} 已上传图片列表</li>
 *   <li>{@link #delete(long)} 主动删除图片</li>
 * </ol>
 *
 * <p>另外提供 {@link #uploadFile(Path)} / {@link #uploadBytes(byte[], String)} 等便捷方法，
 * 内部自动先调用 {@link #getUploadToken()} 再上传，调用方无需关心七牛云细节。</p>
 *
 * <p>仅支持图片类型，30 天有效期，未主动删除的图片由系统自动清理。</p>
 */
public class ImageApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<ImageUploadToken>> TOKEN =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<PageResult<ImageItem>>> LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<Object>> ANY =
            new TypeReference<>() {};

    public ImageApi(PushPlusConfig config, HttpRequester http, AccessKeyManager mgr) {
        super(config, http, mgr);
    }

    /* ------------------------- 1. 获取上传凭证 ------------------------- */

    /** 1. 获取上传凭证。 */
    public ImageUploadToken getUploadToken() {
        return executeOpen("GET", "/api/open/userImage/uploadToken", null, TOKEN);
    }

    /* ------------------------- 2. 上传图片 ------------------------- */

    /**
     * 2. 上传图片到七牛云。
     *
     * <p>使用「获取上传凭证」返回的 {@code uploadUrl} 与 {@code uploadToken} 按七牛云表单上传规范提交。</p>
     *
     * @param token       已获取的上传凭证
     * @param fileBytes   待上传文件的二进制内容
     * @param fileName    文件名（建议带扩展名，如 {@code logo.png}）
     * @param contentType 文件 MIME 类型，可为 {@code null}，将退化为 {@code application/octet-stream}
     * @return 七牛云返回的上传结果（包含 url、key 等）
     */
    public ImageUploadResult upload(ImageUploadToken token, byte[] fileBytes, String fileName, String contentType) {
        if (token == null) {
            throw new PushPlusException("上传凭证 token 不能为 null");
        }
        if (token.getUploadToken() == null || token.getUploadToken().isBlank()) {
            throw new PushPlusException("上传凭证 uploadToken 不能为空");
        }
        if (fileBytes == null || fileBytes.length == 0) {
            throw new PushPlusException("上传文件内容不能为空");
        }
        String uploadUrl = token.getUploadUrl();
        if (uploadUrl == null || uploadUrl.isBlank()) {
            uploadUrl = token.getUploadHost();
        }
        if (uploadUrl == null || uploadUrl.isBlank()) {
            throw new PushPlusException("上传凭证未返回 uploadUrl/uploadHost");
        }

        return upload(uploadUrl, token.getUploadToken(), fileBytes, fileName, contentType);
    }

    /**
     * 2. 上传图片到七牛云（低层方法）。直接指定上传地址与 token。
     *
     * <p>该请求按七牛云规范提交 multipart/form-data，不携带 PushPlus 的 {@code access-key}。</p>
     */
    public ImageUploadResult upload(String uploadUrl, String uploadToken,
                                    byte[] fileBytes, String fileName, String contentType) {
        if (uploadUrl == null || uploadUrl.isBlank()) {
            throw new PushPlusException("uploadUrl 不能为空");
        }
        if (uploadToken == null || uploadToken.isBlank()) {
            throw new PushPlusException("uploadToken 不能为空");
        }
        if (fileBytes == null || fileBytes.length == 0) {
            throw new PushPlusException("上传文件内容不能为空");
        }
        String safeName = (fileName == null || fileName.isBlank()) ? "file" : fileName;
        String mime = (contentType == null || contentType.isBlank()) ? "application/octet-stream" : contentType;

        String boundary = "----PushPlusBoundary" + UUID.randomUUID().toString().replace("-", "");
        byte[] body = buildMultipartBody(boundary, uploadToken, safeName, mime, fileBytes);

        Map<String, String> headers = new LinkedHashMap<>();
        headers.put("Content-Type", "multipart/form-data; boundary=" + boundary);

        HttpResponse resp = http.executeRaw("POST", uploadUrl, headers, body);
        if (!resp.isSuccessful()) {
            throw new PushPlusException(resp.getStatusCode(),
                    "上传图片到七牛云失败: status=" + resp.getStatusCode() + ", body=" + resp.getBody());
        }
        ImageUploadResult result = JsonMapper.fromJson(resp.getBody(), ImageUploadResult.class);
        if (result == null) {
            throw new PushPlusException("七牛云上传响应为空");
        }
        if (!result.isSuccess()) {
            throw new PushPlusException(result.getErrno() == null ? -1 : result.getErrno(),
                    "七牛云上传失败: errno=" + result.getErrno() + ", msg=" + result.getMsg());
        }
        return result;
    }

    /* ------------------------- 便捷上传方法 ------------------------- */

    /**
     * 便捷方法：自动获取上传凭证后上传字节数组。
     *
     * @param fileBytes 文件二进制
     * @param fileName  文件名（用于设置 form-data 的 filename）
     */
    public ImageUploadResult uploadBytes(byte[] fileBytes, String fileName) {
        return uploadBytes(fileBytes, fileName, guessContentTypeByName(fileName));
    }

    /** 便捷方法：自动获取上传凭证后上传字节数组，可指定 MIME。 */
    public ImageUploadResult uploadBytes(byte[] fileBytes, String fileName, String contentType) {
        ImageUploadToken token = getUploadToken();
        return upload(token, fileBytes, fileName, contentType);
    }

    /** 便捷方法：自动获取上传凭证后上传指定路径的文件。 */
    public ImageUploadResult uploadFile(Path filePath) {
        if (filePath == null) {
            throw new PushPlusException("上传文件路径不能为 null");
        }
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new PushPlusException("读取上传文件失败: " + e.getMessage(), e);
        }
        String fileName = filePath.getFileName() == null ? "file" : filePath.getFileName().toString();
        String mime;
        try {
            mime = Files.probeContentType(filePath);
        } catch (IOException ignore) {
            mime = null;
        }
        if (mime == null) {
            mime = guessContentTypeByName(fileName);
        }
        return uploadBytes(bytes, fileName, mime);
    }

    /** 便捷方法：自动获取上传凭证后上传输入流（不关闭传入的流）。 */
    public ImageUploadResult uploadStream(InputStream in, String fileName, String contentType) {
        if (in == null) {
            throw new PushPlusException("输入流不能为 null");
        }
        byte[] bytes;
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            in.transferTo(out);
            bytes = out.toByteArray();
        } catch (IOException e) {
            throw new PushPlusException("读取上传输入流失败: " + e.getMessage(), e);
        }
        return uploadBytes(bytes, fileName, contentType);
    }

    /* ------------------------- 3. 图片列表 ------------------------- */

    /** 3. 图片列表。 */
    public PageResult<ImageItem> list(PageQuery q) {
        return executeOpen("POST", "/api/open/userImage/list", q == null ? new PageQuery() : q, LIST);
    }

    /* ------------------------- 4. 删除图片 ------------------------- */

    /** 4. 主动删除图片；未删除的图片默认 30 天后由系统自动清理。 */
    public void delete(long id) {
        executeOpen("DELETE", appendQuery("/api/open/userImage/delete", Map.of("id", id)), null, ANY);
    }

    /* ------------------------- 内部辅助 ------------------------- */

    private static byte[] buildMultipartBody(String boundary, String token, String fileName,
                                             String contentType, byte[] fileBytes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(fileBytes.length + 512);
        try {
            String tokenPart =
                    "--" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"token\"\r\n\r\n" +
                    token + "\r\n";
            out.write(tokenPart.getBytes(StandardCharsets.UTF_8));

            String filePartHeader =
                    "--" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"file\"; filename=\"" +
                    escapeFileName(fileName) + "\"\r\n" +
                    "Content-Type: " + contentType + "\r\n\r\n";
            out.write(filePartHeader.getBytes(StandardCharsets.UTF_8));
            out.write(fileBytes);
            out.write("\r\n".getBytes(StandardCharsets.UTF_8));

            String tail = "--" + boundary + "--\r\n";
            out.write(tail.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new PushPlusException("拼接 multipart body 失败: " + e.getMessage(), e);
        }
        return out.toByteArray();
    }

    private static String escapeFileName(String name) {
        return name.replace("\"", "_").replace("\r", " ").replace("\n", " ");
    }

    private static String guessContentTypeByName(String name) {
        if (name == null) {
            return "application/octet-stream";
        }
        String lower = name.toLowerCase();
        if (lower.endsWith(".png")) return "image/png";
        if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) return "image/jpeg";
        if (lower.endsWith(".gif")) return "image/gif";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".bmp")) return "image/bmp";
        if (lower.endsWith(".svg")) return "image/svg+xml";
        return "application/octet-stream";
    }
}
