package io.github.perk.pushplus.model.open.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageTokenAddRequest {

    /** 令牌名称；必填。 */
    private String name;

    /** 过期时间，格式 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss；不填默认 2999-12-31。 */
    private String expireTime;
}
