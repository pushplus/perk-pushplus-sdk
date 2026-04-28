package io.github.perk.pushplus.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 回调事件类型。
 */
public enum CallbackEvent {

    /** 消息发送完成。 */
    MESSAGE_COMPLETE("message_complate"),
    /** 群组新增用户。 */
    ADD_TOPIC_USER("add_topic_user"),
    /** 新增好友。 */
    ADD_FRIEND("add_friend");

    private final String code;

    CallbackEvent(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static CallbackEvent of(String code) {
        if (code == null) {
            return null;
        }
        for (CallbackEvent e : values()) {
            if (e.code.equalsIgnoreCase(code)) {
                return e;
            }
        }
        return null;
    }
}
