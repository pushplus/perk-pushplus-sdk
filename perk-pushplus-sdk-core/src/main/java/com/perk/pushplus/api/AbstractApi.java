package com.perk.pushplus.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.exception.PushPlusException;
import com.perk.pushplus.http.HttpRequester;
import com.perk.pushplus.http.HttpResponse;
import com.perk.pushplus.json.JsonMapper;
import com.perk.pushplus.model.ApiResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * API 基类，提供请求执行与统一错误处理。
 */
public abstract class AbstractApi {

    protected final PushPlusConfig config;
    protected final HttpRequester http;

    protected AbstractApi(PushPlusConfig config, HttpRequester http) {
        this.config = config;
        this.http = http;
    }

    /** 拼接绝对 URL。 */
    protected String resolveUrl(String path) {
        if (path.startsWith("http://") || path.startsWith("https://")) {
            return path;
        }
        return config.resolveBaseUrl() + (path.startsWith("/") ? path : "/" + path);
    }

    /** 把 map 拼成 query string。 */
    protected String buildQuery(Map<String, ?> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, ?> e : params.entrySet()) {
            if (e.getValue() == null) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append('&');
            }
            sb.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8))
              .append('=')
              .append(URLEncoder.encode(String.valueOf(e.getValue()), StandardCharsets.UTF_8));
        }
        return sb.toString();
    }

    /** 在 path 上追加 query string。 */
    protected String appendQuery(String path, Map<String, ?> params) {
        String q = buildQuery(params);
        if (q.isEmpty()) {
            return path;
        }
        return path + (path.contains("?") ? "&" : "?") + q;
    }

    /** 创建一个有序 map，便于按顺序拼接 query。 */
    protected Map<String, Object> params() {
        return new LinkedHashMap<>();
    }

    /**
     * 执行请求并返回原始 {@link ApiResponse}（不进行 code 校验）。
     *
     * <p>注意：返回的 {@link ApiResponse#getData()} 在业务失败时可能为 {@code null}（即使
     * 服务端在 {@code data} 字段写入了字符串错误描述，本方法也不会强行反序列化为 {@code T}，
     * 以避免因类型不匹配抛出 JSON 解析异常掩盖真实业务错误）。业务错误的详细文本可通过
     * {@link ApiResponse#getMsg()} 与 {@link PushPlusException#getMessage()} 获取。</p>
     */
    protected <T> ApiResponse<T> execute(String method, String path, Map<String, String> headers,
                                         Object body, TypeReference<ApiResponse<T>> typeRef) {
        String url = resolveUrl(path);
        String json = body == null ? null : JsonMapper.toJson(body);
        HttpResponse resp = http.execute(method, url, headers, json);

        if (!resp.isSuccessful()) {
            throw new PushPlusException(resp.getStatusCode(),
                    "PushPlus 接口 HTTP 调用失败: status=" + resp.getStatusCode() + ", body=" + resp.getBody());
        }
        return parseApiResponse(resp.getBody(), typeRef);
    }

    /** 执行请求并直接返回 data；非 200 抛出异常。 */
    protected <T> T executeForData(String method, String path, Map<String, String> headers,
                                   Object body, TypeReference<ApiResponse<T>> typeRef) {
        ApiResponse<T> resp = execute(method, path, headers, body, typeRef);
        if (!resp.isSuccess()) {
            throw new PushPlusException(resp.getCode() == null ? -1 : resp.getCode(),
                    buildBusinessErrorMessage("PushPlus 接口业务失败", resp));
        }
        return resp.getData();
    }

    /**
     * 两阶段解析 PushPlus 统一响应：
     * <ol>
     *   <li>先把整个响应反序列化为 {@code ApiResponse<JsonNode>}，避免 {@code data} 在业务失败
     *       时被服务端写成字符串（如 {@code "群组不存在"}）而触发 Jackson 类型不匹配异常。</li>
     *   <li>仅在 {@code code == 200} 时，把 {@code data} 节点按目标业务类型反序列化。</li>
     *   <li>{@code code != 200} 时，把 {@code data} 节点的文本（如错误描述）保留到
     *       {@link ApiResponse#getMsg()} 上（在原 {@code msg} 之外追加），便于上层抛出友好异常。</li>
     * </ol>
     */
    @SuppressWarnings("unchecked")
    private <T> ApiResponse<T> parseApiResponse(String responseBody, TypeReference<ApiResponse<T>> typeRef) {
        ApiResponse<JsonNode> raw = JsonMapper.fromJson(responseBody, RAW_API_RESPONSE);
        if (raw == null) {
            throw new PushPlusException("PushPlus 接口返回为空");
        }

        ApiResponse<T> result = new ApiResponse<>();
        result.setCode(raw.getCode());
        result.setMsg(raw.getMsg());

        JsonNode dataNode = raw.getData();
        if (result.isSuccess()) {
            if (dataNode == null || dataNode.isNull()) {
                result.setData(null);
                return result;
            }
            JavaType dataType = resolveDataType(typeRef);
            ObjectMapper mapper = JsonMapper.getMapper();
            try {
                T data = mapper.convertValue(dataNode, dataType);
                result.setData(data);
            } catch (IllegalArgumentException e) {
                throw new PushPlusException(
                        "解析 PushPlus 响应 data 字段失败: " + e.getMessage() + ", payload=" + responseBody, e);
            }
            return result;
        }

        // 业务失败场景：把 data 中的字符串/简单值附加到 msg，方便使用者直接拿到错误描述。
        String dataText = extractDataText(dataNode);
        if (dataText != null && !dataText.isEmpty()) {
            String msg = result.getMsg();
            if (msg == null || msg.isEmpty()) {
                result.setMsg(dataText);
            } else if (!msg.contains(dataText)) {
                result.setMsg(msg + ": " + dataText);
            }
        }
        return result;
    }

    /** 从 {@code TypeReference<ApiResponse<T>>} 中解析出 {@code T} 的 {@link JavaType}。 */
    private JavaType resolveDataType(TypeReference<?> apiResponseTypeRef) {
        JavaType apiResponseType = JsonMapper.getMapper().getTypeFactory().constructType(apiResponseTypeRef);
        JavaType dataType = apiResponseType.containedType(0);
        if (dataType == null) {
            // 退化场景：未声明泛型，按 Object 处理。
            return JsonMapper.getMapper().getTypeFactory().constructType(Object.class);
        }
        return dataType;
    }

    /** 业务失败时把 {@code data} 节点尽可能转为可读文本。 */
    private String extractDataText(JsonNode dataNode) {
        if (dataNode == null || dataNode.isNull() || dataNode.isMissingNode()) {
            return null;
        }
        if (dataNode.isValueNode()) {
            return dataNode.asText();
        }
        return dataNode.toString();
    }

    /** 拼装业务失败的异常文案，优先使用 msg 中已经包含的描述。 */
    private String buildBusinessErrorMessage(String prefix, ApiResponse<?> resp) {
        return prefix + ": code=" + resp.getCode() + ", msg=" + resp.getMsg();
    }

    /** 用于第一阶段反序列化的通用类型：{@code ApiResponse<JsonNode>}。 */
    private static final TypeReference<ApiResponse<JsonNode>> RAW_API_RESPONSE =
            new TypeReference<ApiResponse<JsonNode>>() {
            };
}
