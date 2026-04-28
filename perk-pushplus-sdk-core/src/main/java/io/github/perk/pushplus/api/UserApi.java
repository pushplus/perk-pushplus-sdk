package io.github.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.perk.pushplus.access.AccessKeyManager;
import io.github.perk.pushplus.config.PushPlusConfig;
import io.github.perk.pushplus.http.HttpRequester;
import io.github.perk.pushplus.model.ApiResponse;
import io.github.perk.pushplus.model.open.user.SendCount;
import io.github.perk.pushplus.model.open.user.UserInfo;
import io.github.perk.pushplus.model.open.user.UserLimitTime;

/**
 * 开放接口 - 用户接口（文档「三. 用户接口」）。
 */
public class UserApi extends OpenAbstractApi {

    private static final TypeReference<ApiResponse<String>> TOKEN_RESP =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<UserInfo>> INFO_RESP =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<UserLimitTime>> LIMIT_RESP =
            new TypeReference<>() {};
    private static final TypeReference<ApiResponse<SendCount>> COUNT_RESP =
            new TypeReference<>() {};

    public UserApi(PushPlusConfig config, HttpRequester http, AccessKeyManager accessKeyManager) {
        super(config, http, accessKeyManager);
    }

    /** 获取用户 token。 */
    public String getToken() {
        return executeOpen("GET", "/api/open/user/token", null, TOKEN_RESP);
    }

    /** 个人资料详情。 */
    public UserInfo myInfo() {
        return executeOpen("GET", "/api/open/user/myInfo", null, INFO_RESP);
    }

    /** 获取解封剩余时间。 */
    public UserLimitTime getLimitTime() {
        return executeOpen("GET", "/api/open/user/userLimitTime", null, LIMIT_RESP);
    }

    /** 查询当日消息接口请求次数。 */
    public SendCount getSendCount() {
        return executeOpen("GET", "/api/open/user/sendCount", null, COUNT_RESP);
    }
}
