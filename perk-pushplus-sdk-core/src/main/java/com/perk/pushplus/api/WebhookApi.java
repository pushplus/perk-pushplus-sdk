package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.model.ApiResponse;
import com.perk.pushplus.model.PageQuery;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.webhook.WebhookItem;
import com.perk.pushplus.model.open.webhook.WebhookSaveRequest;

import java.util.Map;

/**
 * 开放接口 - webhook 渠道配置（文档「七. 渠道配置接口」 1-4）。
 */
public class WebhookApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<PageResult<WebhookItem>>> LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<WebhookItem>> ITEM =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<Long>> LONG =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<String>> STRING =
            new TypeReference<>() {};

    public WebhookApi(PushPlusConfig config, HttpRequester http, AccessKeyManager mgr) {
        super(config, http, mgr);
    }

    public PageResult<WebhookItem> list(PageQuery q) {
        return executeOpen("POST", "/api/open/webhook/list", q == null ? new PageQuery() : q, LIST);
    }

    public WebhookItem detail(long webhookId) {
        return executeOpen("GET", appendQuery("/api/open/webhook/detail", Map.of("webhookId", webhookId)), null, ITEM);
    }

    /** 新增 webhook，返回新 id。 */
    public Long add(WebhookSaveRequest req) {
        return executeOpen("POST", "/api/open/webhook/add", req, LONG);
    }

    public String edit(WebhookSaveRequest req) {
        return executeOpen("POST", "/api/open/webhook/edit", req, STRING);
    }
}
