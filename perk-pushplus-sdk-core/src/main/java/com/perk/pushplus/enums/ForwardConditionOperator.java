package com.perk.pushplus.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 图形化触发条件运算符。
 */
public enum ForwardConditionOperator {

    EQ("eq", "等于"),
    NE("ne", "不等于"),
    CONTAINS("contains", "包含"),
    NOT_CONTAINS("notContains", "不包含"),
    STARTS_WITH("startsWith", "开头是"),
    ENDS_WITH("endsWith", "结尾是"),
    REGEX("regex", "正则匹配"),
    GT("gt", "大于"),
    GTE("gte", "大于等于"),
    LT("lt", "小于"),
    LTE("lte", "小于等于"),
    IN("in", "在列表中"),
    NOT_IN("notIn", "不在列表中"),
    EMPTY("empty", "为空"),
    NOT_EMPTY("notEmpty", "不为空");

    private final String code;
    private final String description;

    ForwardConditionOperator(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static ForwardConditionOperator of(String code) {
        if (code == null) {
            return null;
        }
        for (ForwardConditionOperator op : values()) {
            if (op.code.equals(code)) {
                return op;
            }
        }
        return null;
    }
}
