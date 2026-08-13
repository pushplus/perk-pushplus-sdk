package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.json.JsonMapper;
import com.perk.pushplus.model.ApiResponse;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.doc.DocListItem;
import com.perk.pushplus.model.open.doc.DocListQuery;
import com.perk.pushplus.model.open.excel.ExcelContent;
import com.perk.pushplus.model.open.excel.ExcelVo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 开放接口 - push 表格。
 *
 * <p>文档：https://www.pushplus.plus/doc/ecosystem/sheet/</p>
 * <p>基础路径：{@code /push/api/open/excel}</p>
 */
public class ExcelApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<PageResult<DocListItem>>> LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<ExcelVo>> VO =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<ExcelContent>> CONTENT =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<Object>> ANY =
            new TypeReference<>() {};

    public ExcelApi(PushPlusConfig config, HttpRequester http, AccessKeyManager mgr) {
        super(config, http, mgr);
    }

    /** 我的表格分页。 */
    public PageResult<DocListItem> list(DocListQuery query) {
        return executeOpen("POST", "/push/api/open/excel/list",
                query == null ? new DocListQuery() : query, LIST);
    }

    /** 创建空白表格。 */
    public ExcelVo create(String title) {
        return executeOpen("POST", "/push/api/open/excel/create", Map.of("title", title), VO);
    }

    /** 获取表格元信息与整表 JSON 草稿。 */
    public ExcelContent content(String docCode) {
        return executeOpen("GET", appendQuery("/push/api/open/excel/content", Map.of("docCode", docCode)),
                null, CONTENT);
    }

    /**
     * 整表覆盖保存草稿。
     *
     * @param content JSON 字符串，或工作簿对象（SDK 会序列化）
     */
    public ExcelVo saveContent(String docCode, Object content) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("docCode", docCode);
        body.put("content", stringifyJsonContent(content));
        return executeOpen("POST", "/push/api/open/excel/saveContent", body, VO);
    }

    /**
     * 从指定起始单元格起，按二维数组向右向下写入（草稿）。
     *
     * @param range     起始单元格，如 A1
     * @param values    外层为行、内层为列
     * @param sheetName 工作表名称；传 {@code null} 则写入活动表 / 第一张表
     */
    public ExcelVo writeCells(String docCode, String range, List<List<Object>> values, String sheetName) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("docCode", docCode);
        body.put("range", range);
        body.put("values", values);
        if (sheetName != null) {
            body.put("sheetName", sheetName);
        }
        return executeOpen("POST", "/push/api/open/excel/writeCells", body, VO);
    }

    /** 将草稿同步为分享页快照。 */
    public ExcelVo publish(String docCode) {
        return executeOpen("POST", appendQuery("/push/api/open/excel/publish", Map.of("docCode", docCode)),
                null, VO);
    }

    /** 重命名。 */
    public void rename(String docCode, String title) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("docCode", docCode);
        body.put("title", title);
        executeOpen("POST", "/push/api/open/excel/rename", body, ANY);
    }

    /** 删除表格。 */
    public void delete(String docCode) {
        executeOpen("POST", appendQuery("/push/api/open/excel/delete", Map.of("docCode", docCode)), null, ANY);
    }

    /**
     * 更新分享设置。
     *
     * @param sharePerm  0 关闭 / 1 开启（仅可查看）
     * @param shareLogin 0 免登录 / 1 需登录；传 {@code null} 则沿用原值
     */
    public ExcelVo updateShare(String docCode, int sharePerm, Integer shareLogin) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("docCode", docCode);
        body.put("sharePerm", sharePerm);
        if (shareLogin != null) {
            body.put("shareLogin", shareLogin);
        }
        return executeOpen("POST", "/push/api/open/excel/updateShare", body, VO);
    }

    private static String stringifyJsonContent(Object content) {
        if (content == null) {
            return null;
        }
        if (content instanceof String) {
            return (String) content;
        }
        return JsonMapper.toJson(content);
    }
}
