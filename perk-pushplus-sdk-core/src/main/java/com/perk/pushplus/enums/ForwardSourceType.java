package com.perk.pushplus.enums;

/**
 * 消息规则触发来源。
 *
 * <p>0-全部，1-消息接口，2-邮件。</p>
 */
public enum ForwardSourceType {

    ALL(0, "全部"),
    API(1, "消息接口"),
    MAIL(2, "邮件");

    private final int code;
    private final String description;

    ForwardSourceType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ForwardSourceType of(Integer code) {
        if (code == null) {
            return null;
        }
        for (ForwardSourceType type : values()) {
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }
}
