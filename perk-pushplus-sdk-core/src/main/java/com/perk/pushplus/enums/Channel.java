package com.perk.pushplus.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * PushPlus 发送渠道枚举。
 *
 * <p>对应官方文档「发送渠道（channel）枚举」。</p>
 */
public enum Channel {

    /** 微信公众号（默认）。 */
    WECHAT("wechat", "微信公众号"),
    /** 第三方 webhook（企业微信/钉钉/飞书/bark/Gotify/Server酱/IFTTT/WxPusher 等）。 */
    WEBHOOK("webhook", "第三方webhook"),
    /** 企业微信应用。 */
    CP("cp", "企业微信应用"),
    /** 邮箱。 */
    MAIL("mail", "邮箱"),
    /** 短信（收费）。 */
    SMS("sms", "短信"),
    /** 语音（收费）。 */
    VOICE("voice", "语音"),
    /** 浏览器扩展插件 / 桌面应用程序。 */
    EXTENSION("extension", "插件"),
    /** App 渠道（安卓/鸿蒙/iOS）。 */
    APP("app", "App"),
    /** 微信 ClawBot。 */
    CLAWBOT("clawbot", "微信ClawBot"),
    /** 新消息 ClawBot（中国移动 5G 消息）；仅支持中国移动用户。 */
    CMCC("cmcc", "新消息ClawBot"),
    /** QQ 机器人；不带 option 发给自己，option 填配置编码则发到对应 QQ 群。 */
    QQ("qq", "QQ机器人");

    private final String code;
    private final String description;

    Channel(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @JsonCreator
    public static Channel of(String code) {
        if (code == null) {
            return null;
        }
        for (Channel ch : values()) {
            if (ch.code.equalsIgnoreCase(code)) {
                return ch;
            }
        }
        throw new IllegalArgumentException("未知的发送渠道: " + code);
    }
}
