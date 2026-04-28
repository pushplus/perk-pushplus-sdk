package io.github.perk.pushplus.model.open.token;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息 token 下拉选项（仅 id + name）。
 */
@Data
@NoArgsConstructor
public class MessageTokenOption {
    private Long id;
    private String name;
}
