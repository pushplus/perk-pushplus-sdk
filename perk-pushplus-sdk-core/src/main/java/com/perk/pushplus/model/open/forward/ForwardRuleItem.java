package com.perk.pushplus.model.open.forward;

import lombok.Data;
import lombok.NoArgsConstructor;

/** 消息规则列表项。 */
@Data
@NoArgsConstructor
public class ForwardRuleItem {

    private Long id;
    /** 绑定令牌；-1全部令牌，0用户令牌，大于0为消息令牌id。 */
    private Long tokenId;
    private String tokenName;
    private String ruleName;
    /** 触发来源；0-全部，1-消息接口，2-邮件。 */
    private Integer sourceType;
    private String sourceTypeName;
    /** 状态；1-启用，0-停用。 */
    private Integer status;
    /** 匹配顺序，越小越先匹配。 */
    private Integer sort;
    private String conditionExpr;
    private Integer targetCount;
    private String createTime;
}
