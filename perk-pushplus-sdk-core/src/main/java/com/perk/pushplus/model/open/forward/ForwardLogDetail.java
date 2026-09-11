package com.perk.pushplus.model.open.forward;

import lombok.Data;
import lombok.NoArgsConstructor;

/** 触发记录详情。 */
@Data
@NoArgsConstructor
public class ForwardLogDetail {

    private Long id;
    private Long ruleId;
    private String ruleName;
    private Integer sourceType;
    private String sourceTypeName;
    private String requestIp;
    private String requestMethod;
    private String requestHeaders;
    private String requestQuery;
    private String requestBody;
    private String variables;
    private Integer matchResult;
    private String matchResultName;
    private String shortCodes;
    private String errorMessage;
    private String createTime;
}
