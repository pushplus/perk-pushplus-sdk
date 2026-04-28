package io.github.perk.pushplus.model.open.webhook;

import io.github.perk.pushplus.enums.WebhookType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WebhookItem {
    private Long id;
    private String webhookCode;
    private String webhookName;
    private Integer webhookType;
    private String webhookTypeName;
    private String webhookUrl;
    private String createTime;

    /** 自定义类型才返回。 */
    private String httpMethod;
    private String headers;
    private String body;

    public WebhookType getWebhookTypeEnum() {
        return WebhookType.of(webhookType);
    }
}
