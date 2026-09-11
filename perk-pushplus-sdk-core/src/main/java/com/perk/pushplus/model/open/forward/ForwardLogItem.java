package com.perk.pushplus.model.open.forward;

import lombok.Data;
import lombok.NoArgsConstructor;

/** 触发记录列表项。 */
@Data
@NoArgsConstructor
public class ForwardLogItem {

    private Long id;
    private Long ruleId;
    private String ruleName;
    private Integer sourceType;
    private String sourceTypeName;
    private String requestIp;
    private Integer matchResult;
    private String matchResultName;
    private String shortCodes;
    private String errorMessage;
    private String createTime;
}
