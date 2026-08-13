package com.perk.pushplus.enums;

/**
 * push 文档 / 表格打开分享页是否需要登录。
 *
 * <p>0-免登录，1-需登录。</p>
 */
public enum ShareLogin {

    ANONYMOUS(0, "免登录"),
    REQUIRED(1, "需登录");

    private final int code;
    private final String description;

    ShareLogin(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ShareLogin of(Integer code) {
        if (code == null) {
            return null;
        }
        for (ShareLogin s : values()) {
            if (s.code == code) {
                return s;
            }
        }
        return null;
    }
}
