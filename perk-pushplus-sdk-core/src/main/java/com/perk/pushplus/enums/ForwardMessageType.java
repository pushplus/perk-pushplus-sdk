package com.perk.pushplus.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 消息规则发送目标的消息类型。
 *
 * <p>one-一对一，topic-一对多，friend-好友消息。也支持写成 {@code {{变量名}}}。</p>
 */
public enum ForwardMessageType {

    ONE("one", "一对一"),
    TOPIC("topic", "一对多"),
    FRIEND("friend", "好友消息");

    private final String code;
    private final String description;

    ForwardMessageType(String code, String description) {
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
    public static ForwardMessageType of(String code) {
        if (code == null) {
            return null;
        }
        for (ForwardMessageType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
