package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.model.ApiResponse;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.form.FormDetail;
import com.perk.pushplus.model.open.form.FormListItem;
import com.perk.pushplus.model.open.form.FormListQuery;
import com.perk.pushplus.model.open.form.FormPublishDiff;
import com.perk.pushplus.model.open.form.FormPublishResult;
import com.perk.pushplus.model.open.form.FormSaveRequest;

import java.util.Map;

/**
 * 开放接口 - push 表单。
 *
 * <p>文档：https://www.pushplus.plus/doc/ecosystem/form/</p>
 * <p>基础路径：{@code /push/api/open/form}</p>
 */
public class FormApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<PageResult<FormListItem>>> LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<FormListItem>> ITEM =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<FormDetail>> DETAIL =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<FormPublishDiff>> DIFF =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<FormPublishResult>> PUBLISH =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<Object>> ANY =
            new TypeReference<>() {};

    public FormApi(PushPlusConfig config, HttpRequester http, AccessKeyManager mgr) {
        super(config, http, mgr);
    }

    /** 我的表单分页。 */
    public PageResult<FormListItem> list(FormListQuery query) {
        return executeOpen("POST", "/push/api/open/form/list",
                query == null ? new FormListQuery() : query, LIST);
    }

    /** 创建空白表单（草稿）。 */
    public FormListItem create(String title) {
        return executeOpen("POST", "/push/api/open/form/create", Map.of("title", title), ITEM);
    }

    /** 基于已有表单复制一份新草稿。 */
    public FormListItem copy(long id) {
        return executeOpen("POST", appendQuery("/push/api/open/form/copy", Map.of("id", id)), null, ITEM);
    }

    /** 保存表单设计（仅更新草稿；已发布需再调用 publish）。 */
    public void save(FormSaveRequest req) {
        executeOpen("POST", "/push/api/open/form/save", req, ANY);
    }

    /** 表单详情（含草稿题目、主题、设置）。 */
    public FormDetail detail(long id) {
        return executeOpen("GET", appendQuery("/push/api/open/form/detail", Map.of("id", id)), null, DETAIL);
    }

    /** 草稿与发布快照的题目差异。 */
    public FormPublishDiff publishDiff(long id) {
        return executeOpen("GET", appendQuery("/push/api/open/form/publishDiff", Map.of("id", id)), null, DIFF);
    }

    /** 发布表单，开始收集。 */
    public FormPublishResult publish(long id) {
        return executeOpen("POST", appendQuery("/push/api/open/form/publish", Map.of("id", id)), null, PUBLISH);
    }

    /** 停止收集。 */
    public void stop(long id) {
        executeOpen("POST", appendQuery("/push/api/open/form/stop", Map.of("id", id)), null, ANY);
    }

    /** 删除表单（不可恢复）。 */
    public void delete(long id) {
        executeOpen("POST", appendQuery("/push/api/open/form/delete", Map.of("id", id)), null, ANY);
    }
}
