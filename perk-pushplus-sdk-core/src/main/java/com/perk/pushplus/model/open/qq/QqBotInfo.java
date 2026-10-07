package com.perk.pushplus.model.open.qq;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QQ 机器人详情。
 */
@Data
@NoArgsConstructor
public class QqBotInfo {
    private String botId;
    private String username;
    private String avatar;
    private String botAppId;
    /** 官方分享链接，可用于拉机器人进群。 */
    private String shareUrl;
    /** 1-官方机器人，2-自有机器人。 */
    private Integer botType;
}
