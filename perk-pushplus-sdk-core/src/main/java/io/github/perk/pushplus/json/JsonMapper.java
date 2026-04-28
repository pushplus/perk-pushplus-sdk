package io.github.perk.pushplus.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.github.perk.pushplus.exception.PushPlusException;

/**
 * SDK 内部使用的 JSON 工具，基于 Jackson。
 *
 * <p>统一规则：</p>
 * <ul>
 *   <li>属性命名：camelCase（与 PushPlus 接口保持一致）</li>
 *   <li>序列化时忽略 {@code null}</li>
 *   <li>反序列化时忽略未知字段（PushPlus 接口字段经常增量演进）</li>
 *   <li>支持 java.time.* 类型</li>
 * </ul>
 */
public final class JsonMapper {

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE);
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    private JsonMapper() {
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }

    public static String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return MAPPER.writeValueAsString(value);
        } catch (Exception e) {
            throw new PushPlusException("序列化对象到 JSON 失败: " + e.getMessage(), e);
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new PushPlusException("解析 JSON 失败: " + e.getMessage() + ", payload=" + json, e);
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> typeRef) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return MAPPER.readValue(json, typeRef);
        } catch (Exception e) {
            throw new PushPlusException("解析 JSON 失败: " + e.getMessage() + ", payload=" + json, e);
        }
    }
}
