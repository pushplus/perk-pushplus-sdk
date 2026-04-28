package com.perk.pushplus.enums;

/**
 * 消息投递状态。
 *
 * <p>0-未发送/未投递，1-发送中，2-发送成功，3-发送失败。</p>
 */
public enum SendStatus {

    NOT_SENT(0, "未发送"),
    SENDING(1, "发送中"),
    SUCCESS(2, "发送成功"),
    FAILED(3, "发送失败");

    private final int code;
    private final String description;

    SendStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static SendStatus of(Integer code) {
        if (code == null) {
            return null;
        }
        for (SendStatus s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        return null;
    }
}
