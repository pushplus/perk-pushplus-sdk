package io.github.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import io.github.perk.pushplus.config.PushPlusConfig;
import io.github.perk.pushplus.exception.PushPlusException;
import io.github.perk.pushplus.http.HttpRequester;
import io.github.perk.pushplus.model.ApiResponse;
import io.github.perk.pushplus.model.open.access.AccessKeyResult;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AccessKey 接口。
 *
 * <p>对应文档「一. 获取 AccessKey」。</p>
 */
public class AccessKeyApi extends AbstractApi {

    private static final TypeReference<ApiResponse<AccessKeyResult>> RESP =
            new TypeReference<>() {};

    public AccessKeyApi(PushPlusConfig config, HttpRequester http) {
        super(config, http);
    }

    /**
     * 使用配置中的 token + secretKey 获取 AccessKey。
     */
    public AccessKeyResult getAccessKey() {
        return getAccessKey(config.getToken(), config.getSecretKey());
    }

    /**
     * 使用指定 token + secretKey 获取 AccessKey。
     *
     * @param token     用户 token（不支持消息 token）
     * @param secretKey 用户密钥
     */
    public AccessKeyResult getAccessKey(String token, String secretKey) {
        if (token == null || token.isBlank()) {
            throw new PushPlusException("获取 AccessKey 需要 token");
        }
        if (secretKey == null || secretKey.isBlank()) {
            throw new PushPlusException("获取 AccessKey 需要 secretKey");
        }
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("token", token);
        body.put("secretKey", secretKey);
        return executeForData("POST", "/api/common/openApi/getAccessKey", null, body, RESP);
    }
}
