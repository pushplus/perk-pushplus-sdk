package com.perk.pushplus.enums;

/**
 * push 文档 / 表格分享权限。
 *
 * <p>0-关闭分享，1-开启分享（仅可查看）。</p>
 */
public enum SharePerm {

    CLOSED(0, "关闭"),
    VIEW(1, "仅可查看");

    private final int code;
    private final String description;

    SharePerm(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static SharePerm of(Integer code) {
        if (code == null) {
            return null;
        }
        for (SharePerm s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        return null;
    }
}
