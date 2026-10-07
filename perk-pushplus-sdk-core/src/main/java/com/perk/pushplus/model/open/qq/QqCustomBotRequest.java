package com.perk.pushplus.model.open.qq;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 校验/添加/修改自有 QQ 机器人请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QqCustomBotRequest {
    /** QQ 开放平台 AppID，必填，最多 32 个字符。 */
    private String botAppId;
    /** QQ 开放平台 AppSecret，必填，最多 64 个字符。 */
    private String appSecret;
}
