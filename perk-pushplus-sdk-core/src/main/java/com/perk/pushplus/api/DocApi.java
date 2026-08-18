package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.exception.PushPlusException;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.http.MultipartBody;
import com.perk.pushplus.model.ApiResponse;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.doc.DocContent;
import com.perk.pushplus.model.open.doc.DocListItem;
import com.perk.pushplus.model.open.doc.DocListQuery;
import com.perk.pushplus.model.open.doc.DocVo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 开放接口 - push 文档。
 *
 * <p>文档：https://www.pushplus.plus/doc/ecosystem/doc/</p>
 * <p>基础路径：{@code /push/api/open/doc}</p>
 * <p>文档开放接口不单独提供推送接口。发布后请通过 {@link MessageApi} 推送分享页：
 * {@code template=doc}，{@code pushId=docCode}。</p>
 */
public class DocApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<PageResult<DocListItem>>> LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<DocVo>> VO =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<DocContent>> CONTENT =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<Object>> ANY =
            new TypeReference<>() {};

    public DocApi(PushPlusConfig config, HttpRequester http, AccessKeyManager mgr) {
        super(config, http, mgr);
    }

    /** 我的文档分页。 */
    public PageResult<DocListItem> list(DocListQuery query) {
        return executeOpen("POST", "/push/api/open/doc/list",
                query == null ? new DocListQuery() : query, LIST);
    }

    /** 创建空白文档。 */
    public DocVo create(String title) {
        return executeOpen("POST", "/push/api/open/doc/create", Map.of("title", title), VO);
    }

    /**
     * 导入 Word（.docx）创建文档。
     *
     * <p>标题默认取文件名；创建后默认关闭分享，需再调用 {@link #publish(String)} 才会同步到分享页。</p>
     */
    public DocVo importWord(Path filePath) {
        if (filePath == null) {
            throw new PushPlusException("上传文件路径不能为 null");
        }
        byte[] bytes;
        try {
            bytes = Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new PushPlusException("读取上传文件失败: " + e.getMessage(), e);
        }
        String fileName = filePath.getFileName() == null ? "document.docx" : filePath.getFileName().toString();
        return importWord(bytes, fileName);
    }

    /**
     * 导入 Word（.docx）创建文档。
     *
     * @param fileBytes 文件二进制，仅支持 .docx，不超过 2MB
     * @param fileName  文件名（建议带 .docx 扩展名）
     */
    public DocVo importWord(byte[] fileBytes, String fileName) {
        String name = (fileName == null || fileName.isBlank()) ? "document.docx" : fileName;
        MultipartBody mp = MultipartBody.file(name, guessDocxContentType(name), fileBytes);
        return executeOpenMultipart("/push/api/open/doc/import", mp, VO);
    }

    /** 获取文档元信息与 HTML 草稿正文。 */
    public DocContent content(String docCode) {
        return executeOpen("GET", appendQuery("/push/api/open/doc/content", Map.of("docCode", docCode)),
                null, CONTENT);
    }

    /** 保存 HTML 草稿（不影响分享页，需再 publish）。 */
    public DocVo saveContent(String docCode, String content) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("docCode", docCode);
        body.put("content", content);
        return executeOpen("POST", "/push/api/open/doc/saveContent", body, VO);
    }

    /** 将草稿同步为分享页快照。 */
    public DocVo publish(String docCode) {
        return executeOpen("POST", appendQuery("/push/api/open/doc/publish", Map.of("docCode", docCode)),
                null, VO);
    }

    /** 重命名。 */
    public void rename(String docCode, String title) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("docCode", docCode);
        body.put("title", title);
        executeOpen("POST", "/push/api/open/doc/rename", body, ANY);
    }

    /** 删除文档。 */
    public void delete(String docCode) {
        executeOpen("POST", appendQuery("/push/api/open/doc/delete", Map.of("docCode", docCode)), null, ANY);
    }

    /**
     * 更新分享设置。
     *
     * @param sharePerm  0 关闭 / 1 开启（仅可查看）
     * @param shareLogin 0 免登录 / 1 需登录；传 {@code null} 则沿用原值
     */
    public DocVo updateShare(String docCode, int sharePerm, Integer shareLogin) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("docCode", docCode);
        body.put("sharePerm", sharePerm);
        if (shareLogin != null) {
            body.put("shareLogin", shareLogin);
        }
        return executeOpen("POST", "/push/api/open/doc/updateShare", body, VO);
    }

    private static String guessDocxContentType(String name) {
        String lower = name == null ? "" : name.toLowerCase();
        if (lower.endsWith(".docx")) {
            return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }
        return "application/octet-stream";
    }
}
