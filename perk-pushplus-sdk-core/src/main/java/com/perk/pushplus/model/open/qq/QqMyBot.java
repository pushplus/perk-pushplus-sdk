package com.perk.pushplus.model.open.qq;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户可用的 QQ 机器人及其绑定状态。
 */
@Data
@NoArgsConstructor
public class QqMyBot {
    private String botAppId;
    private String botId;
    private String username;
    private String avatar;
    /** 官方分享链接，可用于拉机器人进群。 */
    private String shareUrl;
    /** 1-官方机器人，2-自有机器人。 */
    private Integer botType;
    /** 0-未绑定，1-已绑定。 */
    private Integer isBind;
    /** 1-可接收，0-用户已关闭单聊接收。 */
    private Integer receiveStatus;
    /** 1-默认机器人；发送时不填 option 即用默认机器人发给自己。 */
    private Integer isDefault;
    private String bindTime;
}
