package com.perk.pushplus.enums;

/**
 * 模板变量来源。
 *
 * <p>1-请求头，2-Query参数，3-请求体，4-URL路径，5-主题（邮件）。</p>
 */
public enum ForwardVarSourceType {

    HEADER(1, "请求头"),
    QUERY(2, "Query参数"),
    BODY(3, "请求体"),
    PATH(4, "URL路径"),
    SUBJECT(5, "主题（邮件）");

    private final int code;
    private final String description;

    ForwardVarSourceType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ForwardVarSourceType of(Integer code) {
        if (code == null) {
            return null;
        }
        for (ForwardVarSourceType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
