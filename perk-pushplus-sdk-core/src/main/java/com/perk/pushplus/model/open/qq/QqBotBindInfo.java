package com.perk.pushplus.model.open.qq;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QQ 机器人绑定状态。
 */
@Data
@NoArgsConstructor
public class QqBotBindInfo {
    /** 0-未绑定，1-已绑定。 */
    private Integer isBind;
    /** 1-可接收，0-用户已关闭单聊接收。 */
    private Integer receiveStatus;
    private String createTime;
    private QqBotInfo botInfo;
}
