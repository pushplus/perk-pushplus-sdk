package io.github.perk.pushplus.model.open.message;

import io.github.perk.pushplus.enums.Channel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 开放接口「消息列表」中的单条消息。
 */
@Data
@NoArgsConstructor
public class MessageItem {

    private String topicName;
    /** 消息类型：1-一对一消息，2-一对多消息。 */
    private Integer messageType;
    private String title;
    private String shortCode;
    private Channel channel;
    private String updateTime;
}
