package com.perk.pushplus.model.open.webhook;

import com.perk.pushplus.enums.WebhookType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新增/修改 webhook 请求体（id 仅修改时使用）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebhookSaveRequest {

    private Long id;
    private String webhookCode;
    private String webhookName;
    /** 见 {@link WebhookType}。 */
    private Integer webhookType;
    private String webhookUrl;

    /** 自定义类型时使用：HTTP 方法。 */
    private String httpMethod;
    private String headers;
    private String body;
}
