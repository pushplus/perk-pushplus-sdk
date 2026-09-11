package com.perk.pushplus.model.open.forward;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/** 测试消息规则请求。不会真正发送消息。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForwardRuleTestRequest {

    /** 模拟来源；1-消息接口，2-邮件。 */
    private Integer sourceType;
    private String contentType;
    private Map<String, Object> headers;
    private Map<String, Object> query;
    private String body;
    private String title;
    private String mailFrom;
    private String mailTo;
    private String mailCc;
    private ForwardCondition condition;
    private String conditionExpr;
    private String titleTemplate;
    private String contentTemplate;
    private String template;
    private String pre;
    private List<ForwardVariable> variables;
}
