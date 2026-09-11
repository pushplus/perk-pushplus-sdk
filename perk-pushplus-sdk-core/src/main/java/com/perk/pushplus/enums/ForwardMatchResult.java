package com.perk.pushplus.enums;

/**
 * 消息规则触发记录匹配结果。
 *
 * <p>0-条件不满足，1-已转发，2-频率限制，3-不在触发时间段，4-执行异常。</p>
 */
public enum ForwardMatchResult {

    NOT_MATCHED(0, "条件不满足"),
    FORWARDED(1, "已转发"),
    RATE_LIMITED(2, "频率限制"),
    OUT_OF_TIME(3, "不在触发时间段"),
    ERROR(4, "执行异常");

    private final int code;
    private final String description;

    ForwardMatchResult(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static ForwardMatchResult of(Integer code) {
        if (code == null) {
            return null;
        }
        for (ForwardMatchResult result : values()) {
            if (result.code == code) {
                return result;
            }
        }
        return null;
    }
}
