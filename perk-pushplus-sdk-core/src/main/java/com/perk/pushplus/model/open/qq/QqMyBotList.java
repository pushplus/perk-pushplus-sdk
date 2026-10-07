package com.perk.pushplus.model.open.qq;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 我的 QQ 机器人列表，附带自有机器人的接入信息。
 */
@Data
@NoArgsConstructor
public class QqMyBotList {
    /** 官方机器人在前，自有机器人在后。 */
    private List<QqMyBot> bots;
    /** 已添加的自有机器人数。 */
    private Integer customBotCount;
    /** 可添加的自有机器人上限。 */
    private Integer customBotLimit;
    /** 需在 QQ 开放平台配置的回调地址。 */
    private String webhookUrl;
    /** 需加入 QQ 开放平台 IP 白名单的服务器出口 IP。 */
    private List<String> serverIps;
    /** 需在 QQ 开放平台订阅的事件。 */
    private List<String> events;
}
