package com.perk.pushplus.model.open.forward;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 消息规则详情。 */
@Data
@NoArgsConstructor
public class ForwardRuleDetail {

    private Long id;
    private Long tokenId;
    private String ruleName;
    private Integer sourceType;
    private Integer status;
    private Integer sort;
    private String conditionExpr;
    private ForwardCondition condition;
    private String titleTemplate;
    private String contentTemplate;
    private String template;
    private String pre;
    /** 命中后是否不再匹配后续规则；1-是，0-否。 */
    private Integer stopOnMatch;
    /** 频率限制周期，单位秒；0不限制。 */
    private Integer limitPeriod;
    /** 频率限制周期内最大触发次数；0不限制。 */
    private Integer limitCount;
    private String activeStartTime;
    private String activeEndTime;
    private String activeWeekdays;
    private String remark;
    private List<ForwardVariable> variables;
    private List<ForwardTarget> targets;
    private String createTime;
}
