package com.perk.pushplus.model.open.cmcc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 新消息 ClawBot 绑定请求。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CmccBindRequest {

    /** 中国移动新消息 Channel API Key，必须以 ak_ 或 app_ 开头。 */
    private String apiKey;
}
