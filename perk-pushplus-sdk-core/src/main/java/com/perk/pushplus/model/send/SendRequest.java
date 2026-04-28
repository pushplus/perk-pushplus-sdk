package com.perk.pushplus.model.send;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.perk.pushplus.enums.Channel;
import com.perk.pushplus.enums.Template;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发送消息请求。
 *
 * <p>对应 {@code /send} 接口。推荐使用 {@link #builder()} 链式构建。</p>
 *
 * <pre>{@code
 * SendRequest req = SendRequest.builder()
 *     .title("告警")
 *     .content("CPU 使用率 > 90%")
 *     .template(Template.MARKDOWN)
 *     .channel(Channel.WECHAT)
 *     .topic("ops")
 *     .build();
 * }</pre>
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SendRequest {

    /**
     * 用户 token 或消息 token。可不填，由 SDK 从 {@code PushPlusConfig} 自动注入。
     */
    private String token;

    /** 消息标题。 */
    private String title;

    /** 消息内容；必填。 */
    private String content;

    /** 群组编码。不填仅发送给自己；channel 为 webhook 时无效。 */
    private String topic;

    /** 发送模板，默认 html。 */
    private Template template;

    /** 发送渠道，默认 wechat。 */
    private Channel channel;

    /** 渠道配置参数（cp/webhook/mail 渠道使用渠道编码）。 */
    private String option;

    /** 异步回调地址。 */
    private String callbackUrl;

    /** 毫秒时间戳；服务器时间大于此时间戳消息不会发送（用于消息时效控制）。 */
    private Long timestamp;

    /** 好友令牌；多个用逗号分隔。 */
    private String to;

    /** 预处理编码（仅会员）。 */
    private String pre;
}
