package com.perk.pushplus.model.open.qq;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QQ 机器人渠道配置列表项。
 */
@Data
@NoArgsConstructor
public class QqBotItem {
    private Long id;
    private String qqName;
    /** 配置编码；发送消息时作为 option 传入。 */
    private String qqCode;
    /** 1-发给自己，2-发到 QQ 群。 */
    private Integer sendType;
    /** sendType=2 时返回。 */
    private Long qqGroupId;
    private String groupRemark;
    private String groupOpenId;
    private String groupName;
    /** 发送使用的机器人 appId。 */
    private String botAppId;
    private String botName;
    private String botAvatar;
    private String updateTime;
}
