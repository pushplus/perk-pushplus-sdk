package io.github.perk.pushplus.model.open.topic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;

/**
 * 群组列表查询。
 *
 * <p>注意官方接口结构是 {current, pageSize, params:{topicType}}。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicListQuery {

    private Integer current;
    private Integer pageSize;
    private Map<String, Object> params;

    /**
     * @param topicType 0-我创建的，1-我加入的
     */
    public static TopicListQuery of(Integer current, Integer pageSize, Integer topicType) {
        return new TopicListQuery(current, pageSize,
                topicType == null ? Collections.emptyMap() : Map.of("topicType", topicType));
    }
}
