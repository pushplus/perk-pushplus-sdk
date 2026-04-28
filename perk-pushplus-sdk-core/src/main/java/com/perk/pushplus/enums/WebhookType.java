package com.perk.pushplus.enums;

/**
 * Webhook 渠道类型。
 *
 * <p>对应开放接口文档「webhook 列表」中的 webhookType 枚举值。</p>
 */
public enum WebhookType {

    WORK_WECHAT_BOT(1, "企业微信机器人"),
    DING_TALK_BOT(2, "钉钉机器人"),
    FEISHU_BOT(3, "飞书机器人"),
    SERVER_CHAN(4, "Server酱"),
    BARK(50, "bark"),
    WORK_WECHAT_APP(6, "企业微信应用"),
    TENCENT_LIGHT_LINK(7, "腾讯轻联"),
    IFTTT(8, "IFTTT"),
    JI_JIAN_YUN(9, "集简云"),
    GOTIFY(10, "Gotify"),
    WX_PUSHER(11, "WxPusher"),
    CUSTOM(12, "自定义");

    private final int code;
    private final String description;

    WebhookType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static WebhookType of(Integer code) {
        if (code == null) {
            return null;
        }
        for (WebhookType t : values()) {
            if (t.code == code) {
                return t;
            }
        }
        return null;
    }
}
