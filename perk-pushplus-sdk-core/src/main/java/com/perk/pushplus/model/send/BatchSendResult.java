package com.perk.pushplus.model.send;

import com.perk.pushplus.enums.Channel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 批量发送的单条渠道结果。
 */
@Data
@NoArgsConstructor
public class BatchSendResult {

    /** 消息流水号；可用于查询发送结果。 */
    private String shortCode;

    /** 业务消息。 */
    private String message;

    /** 业务 code。 */
    private Integer code;

    /** 渠道。 */
    private Channel channel;
}
