package com.perk.pushplus.model.open.forward;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/** 测试消息规则结果。 */
@Data
@NoArgsConstructor
public class ForwardRuleTestResult {

    /** 提取到的全部变量，含内置变量。 */
    private Map<String, Object> variables;
    private Boolean matched;
    private String conditionExpr;
    private String errorMessage;
    private String title;
    private String content;
    private String template;
}
