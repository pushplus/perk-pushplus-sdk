package com.perk.pushplus.model.open.forward;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 新增 / 修改消息规则。修改时 {@code id} 必填，会整体覆盖变量和发送目标。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForwardRuleSaveRequest {

    /** 修改时必填。 */
    private Long id;
    private String ruleName;
    /** 绑定令牌；-1全部令牌，0用户令牌，大于0为消息令牌id。 */
    private Long tokenId;
    /** 触发来源；0-全部，1-消息接口，2-邮件。 */
    private Integer sourceType;
    /** 状态；1-启用，0-停用。 */
    private Integer status;
    private Integer sort;
    private ForwardCondition condition;
    private String conditionExpr;
    private String titleTemplate;
    private String contentTemplate;
    private String template;
    private String pre;
    private Integer stopOnMatch;
    private Integer limitPeriod;
    private Integer limitCount;
    private String activeStartTime;
    private String activeEndTime;
    private String activeWeekdays;
    private String remark;
    private List<ForwardVariable> variables;
    private List<ForwardTarget> targets;
}
