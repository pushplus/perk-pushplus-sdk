package com.perk.pushplus.enums;

/**
 * push 表单状态。
 *
 * <p>0-草稿，1-收集中，2-已停止。</p>
 */
public enum FormStatus {

    DRAFT(0, "草稿"),
    COLLECTING(1, "收集中"),
    STOPPED(2, "已停止");

    private final int code;
    private final String description;

    FormStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static FormStatus of(Integer code) {
        if (code == null) {
            return null;
        }
        for (FormStatus s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        return null;
    }
}
