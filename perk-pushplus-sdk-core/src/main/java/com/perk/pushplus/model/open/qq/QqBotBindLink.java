package com.perk.pushplus.model.open.qq;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * QQ 机器人绑定链接与绑定码。
 */
@Data
@NoArgsConstructor
public class QqBotBindLink {
    /** 带参分享链接，用于生成扫码二维码；已绑定用户再次获取时可能为空。 */
    private String url;
    /** 绑定码。已是好友时扫码收不到加好友事件，需私聊发送该码；认领 QQ 群也用此码。 */
    private String bindCode;
    /** 有效期秒数，默认 300。 */
    private Integer expireSeconds;
    /** 为当前用户分配的官方机器人 appId。 */
    private String botAppId;
    private String botName;
    private String botAvatar;
}
