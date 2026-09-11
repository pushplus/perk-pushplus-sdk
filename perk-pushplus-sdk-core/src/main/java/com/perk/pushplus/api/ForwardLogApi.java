package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.model.ApiResponse;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.forward.ForwardLogDetail;
import com.perk.pushplus.model.open.forward.ForwardLogItem;
import com.perk.pushplus.model.open.forward.ForwardLogListQuery;

import java.util.Map;

/**
 * 开放接口 - 消息规则触发记录（文档「十四. 消息规则接口」第 10、11 节）。
 */
public class ForwardLogApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<PageResult<ForwardLogItem>>> LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<ForwardLogDetail>> DETAIL =
            new TypeReference<>() {};

    public ForwardLogApi(PushPlusConfig config, HttpRequester http, AccessKeyManager mgr) {
        super(config, http, mgr);
    }

    /** 获取触发记录列表。 */
    public PageResult<ForwardLogItem> list(ForwardLogListQuery query) {
        return executeOpen("POST", "/api/open/forwardLog/list",
                query == null ? new ForwardLogListQuery() : query, LIST);
    }

    /** 查看触发记录详情。 */
    public ForwardLogDetail detail(long logId) {
        return executeOpen("GET", appendQuery("/api/open/forwardLog/detail", Map.of("logId", logId)), null, DETAIL);
    }
}
