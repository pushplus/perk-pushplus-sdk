package com.perk.pushplus.model.open.forward;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 图形化触发条件中的单条比较。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForwardConditionItem {

    /** 参与比较的变量名，模板变量或内置变量。 */
    private String varName;
    /** 运算符，如 eq / contains / regex。 */
    private String operator;
    /** 比较值；empty/notEmpty 时可为空；in/notIn 用逗号分隔。 */
    private String value;
}
