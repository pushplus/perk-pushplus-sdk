package com.perk.pushplus.model.open.doc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 文档 / 表格分页查询。
 *
 * <p>官方接口结构是 {@code {current, pageSize, params:{keyword, shareEnabled}}}，与主站开放接口一致。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocListQuery {

    /** 当前所在分页数，默认 1。 */
    private Integer current;
    /** 每页大小，默认 20，最大 50。 */
    private Integer pageSize;
    /** 筛选条件，如 keyword、shareEnabled。 */
    private Map<String, Object> params;

    public static DocListQuery of(Integer current, Integer pageSize) {
        return DocListQuery.builder().current(current).pageSize(pageSize).build();
    }

    public static DocListQuery of(Integer current, Integer pageSize, String keyword, Boolean shareEnabled) {
        Map<String, Object> params = new LinkedHashMap<>();
        if (keyword != null) {
            params.put("keyword", keyword);
        }
        if (shareEnabled != null) {
            params.put("shareEnabled", shareEnabled);
        }
        return DocListQuery.builder()
                .current(current)
                .pageSize(pageSize)
                .params(params.isEmpty() ? null : params)
                .build();
    }
}
