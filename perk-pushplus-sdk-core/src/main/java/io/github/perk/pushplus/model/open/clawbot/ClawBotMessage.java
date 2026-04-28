package io.github.perk.pushplus.model.open.clawbot;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClawBotMessage {
    /** 1-文字，3-语音。 */
    private Integer type;
    private String text;
}
