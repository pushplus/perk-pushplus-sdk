package com.perk.pushplus.model.open.forward;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 发送目标。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForwardTarget {

    private Long id;
    private Long ruleId;
    /** 发送渠道；为空则用用户默认渠道。支持 {@code {{变量名}}}。 */
    private String channel;
    /** 渠道配置编码，与 send 接口的 option 含义一致。 */
    private String option;
    /** 消息类型；one / topic / friend。支持 {@code {{变量名}}}。 */
    private String messageType;
    /** 群组编码，一对多时使用。 */
    private String topic;
    /** 好友令牌，逗号分隔，好友消息时使用。 */
    private String to;
    /** 发送顺序。 */
    private Integer sort;
}
