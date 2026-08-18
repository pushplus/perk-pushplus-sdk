package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.perk.pushplus.access.AccessKeyManager;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.exception.PushPlusException;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.http.MultipartBody;
import com.perk.pushplus.model.ApiResponse;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 开放接口基类。会自动在 header 中带上 {@code access-key}，
 * 并在收到 401/403 类业务错误时尝试重试一次（刷新 AccessKey 后重试）。
 */
public abstract class OpenAbstractApi extends AbstractApi {

    public static final String HEADER_ACCESS_KEY = "access-key";

    /** PushPlus AccessKey 失效相关的业务码（用于触发自动重试）。 */
    private static final int CODE_ACCESS_KEY_INVALID = 401;

    protected final AccessKeyManager accessKeyManager;

    protected OpenAbstractApi(PushPlusConfig config, HttpRequester http, AccessKeyManager accessKeyManager) {
        super(config, http);
        this.accessKeyManager = accessKeyManager;
    }

    private Map<String, String> headersWithAccessKey() {
        Map<String, String> h = new LinkedHashMap<>();
        h.put(HEADER_ACCESS_KEY, accessKeyManager.getAccessKey());
        return h;
    }

    /**
     * 执行带 access-key 的请求；当返回 code=401 时自动刷新 key 并重试一次。
     */
    protected <T> T executeOpen(String method, String path, Object body, TypeReference<ApiResponse<T>> typeRef) {
        Map<String, String> headers = headersWithAccessKey();
        ApiResponse<T> resp = execute(method, path, headers, body, typeRef);
        if (resp.isSuccess()) {
            return resp.getData();
        }
        if (resp.getCode() != null && resp.getCode() == CODE_ACCESS_KEY_INVALID) {
            accessKeyManager.invalidate();
            Map<String, String> retryHeaders = headersWithAccessKey();
            ApiResponse<T> retry = execute(method, path, retryHeaders, body, typeRef);
            if (retry.isSuccess()) {
                return retry.getData();
            }
            throw new PushPlusException(retry.getCode() == null ? -1 : retry.getCode(),
                    "PushPlus 开放接口业务失败(重试后): code=" + retry.getCode() + ", msg=" + retry.getMsg());
        }
        throw new PushPlusException(resp.getCode() == null ? -1 : resp.getCode(),
                "PushPlus 开放接口业务失败: code=" + resp.getCode() + ", msg=" + resp.getMsg());
    }

    /**
     * 以 multipart 上传文件（自动携带 access-key；code=401 时刷新后重试一次）。
     */
    protected <T> T executeOpenMultipart(String path, MultipartBody multipart,
                                         TypeReference<ApiResponse<T>> typeRef) {
        if (multipart == null) {
            throw new PushPlusException("multipart 请求体不能为空");
        }
        Map<String, String> extra = new LinkedHashMap<>();
        extra.put("Content-Type", multipart.getContentType());
        return executeOpenRaw("POST", path, extra, multipart.getBody(), typeRef);
    }

    /**
     * 执行带二进制 body 的开放接口请求；当返回 code=401 时自动刷新 key 并重试一次。
     */
    protected <T> T executeOpenRaw(String method, String path, Map<String, String> extraHeaders,
                                   byte[] body, TypeReference<ApiResponse<T>> typeRef) {
        Map<String, String> headers = headersWithAccessKey();
        if (extraHeaders != null) {
            headers.putAll(extraHeaders);
        }
        ApiResponse<T> resp = executeRaw(method, path, headers, body, typeRef);
        if (resp.isSuccess()) {
            return resp.getData();
        }
        if (resp.getCode() != null && resp.getCode() == CODE_ACCESS_KEY_INVALID) {
            accessKeyManager.invalidate();
            Map<String, String> retryHeaders = headersWithAccessKey();
            if (extraHeaders != null) {
                retryHeaders.putAll(extraHeaders);
            }
            ApiResponse<T> retry = executeRaw(method, path, retryHeaders, body, typeRef);
            if (retry.isSuccess()) {
                return retry.getData();
            }
            throw new PushPlusException(retry.getCode() == null ? -1 : retry.getCode(),
                    "PushPlus 开放接口业务失败(重试后): code=" + retry.getCode() + ", msg=" + retry.getMsg());
        }
        throw new PushPlusException(resp.getCode() == null ? -1 : resp.getCode(),
                "PushPlus 开放接口业务失败: code=" + resp.getCode() + ", msg=" + resp.getMsg());
    }
}

