package com.perk.pushplus.model.open.forward;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 图形化触发条件。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForwardCondition {

    /** 条件连接方式；and-全部满足，or-任一满足。 */
    private String logic;
    /** 条件列表；为空表示无条件命中。 */
    private List<ForwardConditionItem> items;
}
