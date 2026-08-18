package com.perk.pushplus.model.open.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 我的表单分页查询。
 *
 * <p>官方接口结构是 {@code {current, pageSize, params:{keyword, status}}}，与主站开放接口一致。</p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormListQuery {

    /** 当前所在分页数，默认 1。 */
    private Integer current;
    /** 每页大小，默认 20，最大 50。 */
    private Integer pageSize;
    /** 筛选条件，如 keyword、status。 */
    private Map<String, Object> params;

    public static FormListQuery of(Integer current, Integer pageSize) {
        return FormListQuery.builder().current(current).pageSize(pageSize).build();
    }

    public static FormListQuery of(Integer current, Integer pageSize, String keyword, Integer status) {
        Map<String, Object> params = new LinkedHashMap<>();
        if (keyword != null) {
            params.put("keyword", keyword);
        }
        if (status != null) {
            params.put("status", status);
        }
        return FormListQuery.builder()
                .current(current)
                .pageSize(pageSize)
                .params(params.isEmpty() ? null : params)
                .build();
    }
}
