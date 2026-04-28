package io.github.perk.pushplus.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * PushPlus 消息模板枚举。
 */
public enum Template {

    /** 默认模板，支持 HTML 文本。 */
    HTML("html"),
    /** 纯文本，不转义 HTML。 */
    TXT("txt"),
    /** 基于 JSON 格式展示。 */
    JSON("json"),
    /** Markdown 格式。 */
    MARKDOWN("markdown"),
    /** 阿里云监控报警定制模板。 */
    CLOUD_MONITOR("cloudMonitor"),
    /** Jenkins 插件定制模板。 */
    JENKINS("jenkins"),
    /** 路由器插件定制模板。 */
    ROUTE("route"),
    /** 支付成功通知模板。 */
    PAY("pay");

    private final String code;

    Template(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @JsonCreator
    public static Template of(String code) {
        if (code == null) {
            return null;
        }
        for (Template t : values()) {
            if (t.code.equalsIgnoreCase(code)) {
                return t;
            }
        }
        throw new IllegalArgumentException("未知的消息模板: " + code);
    }
}
