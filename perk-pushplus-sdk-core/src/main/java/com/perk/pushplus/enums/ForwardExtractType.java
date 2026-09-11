package com.perk.pushplus.enums;

/**
 * 模板变量提取方式。
 *
 * <p>1-序列化数据，2-正则表达式，3-JSONPath，4-原始全文。</p>
 */
public enum ForwardExtractType {

    SERIALIZED(1, "序列化数据"),
    REGEX(2, "正则表达式"),
    JSON_PATH(3, "JSONPath"),
    RAW(4, "原始全文");

    private final int code;
    private final String description;

    ForwardExtractType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ForwardExtractType of(Integer code) {
        if (code == null) {
            return null;
        }
        for (ForwardExtractType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
