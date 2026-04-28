package io.github.perk.pushplus.model.open.access;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取 AccessKey 接口响应。
 */
@Data
@NoArgsConstructor
public class AccessKeyResult {

    /** 访问令牌，后续请求需加到 header 中。 */
    private String accessKey;

    /** 过期时间（单位秒）。 */
    private Long expiresIn;
}
