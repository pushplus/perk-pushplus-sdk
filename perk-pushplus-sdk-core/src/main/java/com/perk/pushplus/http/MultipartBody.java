package com.perk.pushplus.http;

import com.perk.pushplus.exception.PushPlusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 构造仅含一个文件字段的 multipart/form-data 请求体。
 */
public final class MultipartBody {

    private final String contentType;
    private final byte[] body;

    private MultipartBody(String contentType, byte[] body) {
        this.contentType = contentType;
        this.body = body;
    }

    public String getContentType() {
        return contentType;
    }

    public byte[] getBody() {
        return body;
    }

    /**
     * 构造 {@code name="file"} 的表单上传体。
     *
     * @param fileName    文件名（含扩展名）
     * @param contentType 文件 MIME；为空时退化为 {@code application/octet-stream}
     * @param fileBytes   文件内容
     */
    public static MultipartBody file(String fileName, String contentType, byte[] fileBytes) {
        if (fileBytes == null || fileBytes.length == 0) {
            throw new PushPlusException("上传文件内容不能为空");
        }
        String safeName = (fileName == null || fileName.isBlank()) ? "file" : fileName;
        String mime = (contentType == null || contentType.isBlank()) ? "application/octet-stream" : contentType;
        String boundary = "----PushPlusBoundary" + UUID.randomUUID().toString().replace("-", "");

        ByteArrayOutputStream out = new ByteArrayOutputStream(fileBytes.length + 512);
        try {
            String header =
                    "--" + boundary + "\r\n" +
                    "Content-Disposition: form-data; name=\"file\"; filename=\"" +
                    escapeFileName(safeName) + "\"\r\n" +
                    "Content-Type: " + mime + "\r\n\r\n";
            out.write(header.getBytes(StandardCharsets.UTF_8));
            out.write(fileBytes);
            out.write("\r\n".getBytes(StandardCharsets.UTF_8));
            out.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new PushPlusException("拼接 multipart body 失败: " + e.getMessage(), e);
        }
        return new MultipartBody("multipart/form-data; boundary=" + boundary, out.toByteArray());
    }

    private static String escapeFileName(String name) {
        return name.replace("\"", "_").replace("\r", " ").replace("\n", " ");
    }
}
