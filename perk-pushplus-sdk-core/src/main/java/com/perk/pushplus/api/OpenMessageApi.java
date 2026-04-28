package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.exception.PushPlusException;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.model.ApiResponse;
import com.perk.pushplus.model.PageQuery;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.message.MessageItem;
import com.perk.pushplus.model.open.message.SendMessageResult;

/**
 * 开放接口 - 消息接口（文档「二. 消息接口」）。
 */
public class OpenMessageApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<PageResult<MessageItem>>> LIST_RESP =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<SendMessageResult>> RESULT_RESP =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<String>> STRING_RESP =
            new TypeReference<>() {};

    public OpenMessageApi(PushPlusConfig config, HttpRequester http, AccessKeyManager accessKeyManager) {
        super(config, http, accessKeyManager);
    }

    /** 1. 消息列表。 */
    public PageResult<MessageItem> list(PageQuery query) {
        return executeOpen("POST", "/api/open/message/list", query == null ? new PageQuery() : query, LIST_RESP);
    }

    /** 2. 查询消息发送结果。 */
    public SendMessageResult queryResult(String shortCode) {
        if (shortCode == null || shortCode.isBlank()) {
            throw new PushPlusException("shortCode 不能为空");
        }
        var path = appendQuery("/api/open/message/sendMessageResult", java.util.Map.of("shortCode", shortCode));
        return executeOpen("GET", path, null, RESULT_RESP);
    }

    /** 3. 删除消息。 */
    public String delete(String shortCode) {
        if (shortCode == null || shortCode.isBlank()) {
            throw new PushPlusException("shortCode 不能为空");
        }
        var path = appendQuery("/api/open/message/deleteMessage", java.util.Map.of("shortCode", shortCode));
        return executeOpen("DELETE", path, null, STRING_RESP);
    }

    /**
     * 4. 消息详情（HTML 页面 URL）。
     *
     * <p>该接口直接返回 HTML 内容，SDK 仅返回访问 URL，调用方自行决定是否拉取页面内容。</p>
     */
    public String detailUrl(String shortCode) {
        if (shortCode == null || shortCode.isBlank()) {
            throw new PushPlusException("shortCode 不能为空");
        }
        return resolveUrl("/shortMessage/" + shortCode);
    }
}
