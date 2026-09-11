package com.perk.pushplus.enums;

/**
 * 消息规则总开关模式。
 *
 * <p>0-关闭（推送与原来一致），1-开启且未命中时仍按默认方式推送，2-开启且未命中时不推送。</p>
 */
public enum ForwardMode {

    OFF(0, "关闭（推送与原来一致）"),
    ON_FALLBACK(1, "开启，未命中时仍按默认方式推送"),
    ON_STRICT(2, "开启，未命中时不推送");

    private final int code;
    private final String description;

    ForwardMode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ForwardMode of(Integer code) {
        if (code == null) {
            return null;
        }
        for (ForwardMode mode : values()) {
            if (mode.code == code) {
                return mode;
            }
        }
        return null;
    }
}
