package com.perk.pushplus.model.open.qq;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 机器人已加入的 QQ 群。
 */
@Data
@NoArgsConstructor
public class QqGroupItem {
    /** 群编号；新增渠道配置时作为 qqGroupId 使用。 */
    private Long id;
    private String groupOpenId;
    private String groupRemark;
    /** 1-在群，2-群消息接收关闭。 */
    private Integer status;
    /** 群名称，接口未授权时为空。 */
    private String groupName;
    private String groupFingerMemo;
    private String groupClassText;
    private List<String> groupTags;
    private Integer groupMemberNum;
    private String createTime;
}
