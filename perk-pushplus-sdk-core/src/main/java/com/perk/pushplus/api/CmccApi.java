package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.model.ApiResponse;
import com.perk.pushplus.model.open.cmcc.CmccBindRequest;
import com.perk.pushplus.model.open.cmcc.CmccInfo;

/**
 * 开放接口 - 新消息 ClawBot（文档「九. 新消息ClawBot接口」）。
 *
 * <p>需先在手机 5G 消息的「新消息ClawBot」应用号中获取 Channel API Key，再调用绑定接口。
 * 发送消息时 channel 传 {@code cmcc}。仅支持中国移动用户。</p>
 */
public class CmccApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<CmccInfo>> INFO =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<Object>> ANY =
            new TypeReference<>() {};

    public CmccApi(PushPlusConfig config, HttpRequester http, AccessKeyManager mgr) {
        super(config, http, mgr);
    }

    /** 1. 绑定新消息 ClawBot。apiKey 必须以 ak_ 或 app_ 开头。 */
    public void bind(String apiKey) {
        executeOpen("POST", "/api/open/cmcc/bind", CmccBindRequest.builder().apiKey(apiKey).build(), ANY);
    }

    /** 2. 查询绑定状态。 */
    public CmccInfo info() {
        return executeOpen("GET", "/api/open/cmcc/info", null, INFO);
    }

    /** 3. 解绑新消息 ClawBot。 */
    public void unbind() {
        executeOpen("GET", "/api/open/cmcc/unbind", null, ANY);
    }

    /** 4. 发送测试消息。未绑定会返回「未绑定新消息ClawBot」。 */
    public void sendTest() {
        executeOpen("GET", "/api/open/cmcc/test", null, ANY);
    }
}
