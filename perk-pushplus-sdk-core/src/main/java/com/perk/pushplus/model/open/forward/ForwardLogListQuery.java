package com.perk.pushplus.model.open.forward;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 触发记录分页查询。
 *
 * <p>官方接口结构是 {@code {current, pageSize, params:{ruleId, matchResult}}}。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForwardLogListQuery {

    /** 当前所在分页数，默认 1。 */
    private Integer current;
    /** 每页大小，默认 20，最大 50。 */
    private Integer pageSize;
    /** 筛选条件，如 ruleId、matchResult。 */
    private Map<String, Object> params;

    public static ForwardLogListQuery of(Integer current, Integer pageSize) {
        return ForwardLogListQuery.builder().current(current).pageSize(pageSize).build();
    }

    public static ForwardLogListQuery of(Integer current, Integer pageSize, Long ruleId, Integer matchResult) {
        Map<String, Object> params = new LinkedHashMap<>();
        if (ruleId != null) {
            params.put("ruleId", ruleId);
        }
        if (matchResult != null) {
            params.put("matchResult", matchResult);
        }
        return ForwardLogListQuery.builder()
                .current(current)
                .pageSize(pageSize)
                .params(params.isEmpty() ? null : params)
                .build();
    }
}
