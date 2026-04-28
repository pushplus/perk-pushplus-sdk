package com.perk.pushplus.model.callback;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.perk.pushplus.enums.CallbackEvent;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PushPlus 回调统一载体。
 *
 * <p>使用 {@code CallbackParser.parse(json)} 解析回调请求体后，
 * 根据 {@link #getEvent()} 判断事件类型并取对应的字段。</p>
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallbackPayload {

    /** 回调事件名称。 */
    private CallbackEvent event;

    /** 消息完成事件。 */
    private MessageCompleteInfo messageInfo;

    /** 群组新增用户事件。 */
    private TopicUserInfo topicUserInfo;

    /** 新增好友事件。 */
    private FriendInfo friendInfo;

    /** 自定义二维码参数（仅 add_friend 事件有值）。 */
    private String qrCode;
}
