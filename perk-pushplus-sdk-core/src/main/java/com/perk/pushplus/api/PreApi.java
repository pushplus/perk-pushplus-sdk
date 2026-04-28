package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.model.ApiResponse;
import com.perk.pushplus.model.PageQuery;
import com.perk.pushplus.model.PageResult;
import com.perk.pushplus.model.open.pre.PreDetail;
import com.perk.pushplus.model.open.pre.PreItem;
import com.perk.pushplus.model.open.pre.PreSaveRequest;
import com.perk.pushplus.model.open.pre.PreTestRequest;

import java.util.Map;

/**
 * 开放接口 - 预处理信息（文档「十一. 预处理信息接口」）。注：需开通会员。
 */
public class PreApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<PageResult<PreItem>>> LIST =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<PreDetail>> DETAIL =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<Long>> LONG =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<String>> STRING =
            new TypeReference<>() {};

    public PreApi(PushPlusConfig config, HttpRequester http, AccessKeyManager mgr) {
        super(config, http, mgr);
    }

    public PageResult<PreItem> list(PageQuery q) {
        return executeOpen("POST", "/api/open/pre/list", q == null ? new PageQuery() : q, LIST);
    }

    public PreDetail detail(long preId) {
        return executeOpen("GET", appendQuery("/api/open/pre/detail", Map.of("preId", preId)), null, DETAIL);
    }

    public Long add(PreSaveRequest req) {
        return executeOpen("POST", "/api/open/pre/add", req, LONG);
    }

    public String edit(PreSaveRequest req) {
        return executeOpen("POST", "/api/open/pre/edit", req, STRING);
    }

    public String delete(long preId) {
        return executeOpen("DELETE", appendQuery("/api/open/pre/delete", Map.of("preId", preId)), null, STRING);
    }

    /** 测试预处理代码，返回处理后的消息。 */
    public String test(PreTestRequest req) {
        return executeOpen("POST", "/api/open/pre/test", req, STRING);
    }
}
