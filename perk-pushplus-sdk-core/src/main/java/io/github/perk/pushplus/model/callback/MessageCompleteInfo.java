package io.github.perk.pushplus.model.callback;

import io.github.perk.pushplus.enums.SendStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息完成回调中的 messageInfo 字段。
 */
@Data
@NoArgsConstructor
public class MessageCompleteInfo {

    /** 推送错误内容（如有）。 */
    private String message;

    /** 消息流水号。 */
    private String shortCode;

    /** 发送状态：0-未发送，1-发送中，2-发送成功，3-发送失败。 */
    private Integer sendStatus;

    public SendStatus getSendStatusEnum() {
        return SendStatus.of(sendStatus);
    }
}
