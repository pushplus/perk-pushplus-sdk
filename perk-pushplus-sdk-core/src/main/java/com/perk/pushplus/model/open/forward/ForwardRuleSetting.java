package com.perk.pushplus.model.open.forward;

import lombok.Data;
import lombok.NoArgsConstructor;

/** 消息规则总开关。 */
@Data
@NoArgsConstructor
public class ForwardRuleSetting {

    /** 0-关闭，1-开启且未命中仍推送，2-开启且未命中不推送。 */
    private Integer mode;
}
