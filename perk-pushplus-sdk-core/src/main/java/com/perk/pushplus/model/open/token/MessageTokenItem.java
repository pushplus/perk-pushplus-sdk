package com.perk.pushplus.model.open.token;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 消息 token 列表项。
 */
@Data
@NoArgsConstructor
public class MessageTokenItem {
    private Long id;
    private String name;
    private String expireTime;
    private String token;
}
