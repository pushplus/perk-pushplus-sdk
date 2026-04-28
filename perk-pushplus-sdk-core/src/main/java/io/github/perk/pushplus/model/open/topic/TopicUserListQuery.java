package io.github.perk.pushplus.model.open.topic;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicUserListQuery {

    private Integer current;
    private Integer pageSize;
    private Map<String, Object> params;

    public static TopicUserListQuery of(Integer current, Integer pageSize, long topicId) {
        return new TopicUserListQuery(current, pageSize, Map.of("topicId", topicId));
    }
}
