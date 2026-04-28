package io.github.perk.pushplus.model.open.user;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 当日消息接口请求次数。
 */
@Data
@NoArgsConstructor
public class SendCount {
    private Integer wechatSendCount;
    private Integer cpSendCount;
    private Integer webhookSendCount;
    private Integer mailSendCount;
}
