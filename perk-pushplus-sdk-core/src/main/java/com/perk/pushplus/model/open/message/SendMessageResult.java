package com.perk.pushplus.model.open.message;

import com.perk.pushplus.enums.SendStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询消息发送结果响应。
 */
@Data
@NoArgsConstructor
public class SendMessageResult {

    /** 0-未投递，1-发送中，2-已发送，3-发送失败。 */
    private Integer status;
    private String errorMessage;
    private String updateTime;

    public SendStatus getStatusEnum() {
        return SendStatus.of(status);
    }
}
