package io.github.perk.pushplus.model.open.clawbot;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClawBotInfo {
    private String createTime;
    /** 是否有对话令牌（文档为字符串/数字混用，统一用 Integer 兼容）。 */
    private Integer haveContextToken;
}
