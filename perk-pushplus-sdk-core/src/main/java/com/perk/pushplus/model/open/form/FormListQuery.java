package com.perk.pushplus.model.open.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 我的表单分页查询。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormListQuery {

    /** 页码，从 1 开始。 */
    private Integer pageNum;
    /** 每页条数。 */
    private Integer pageSize;
    /** 按标题关键词搜索。 */
    private String keyword;
    /** 表单状态：0草稿 / 1收集中 / 2已停止。 */
    private Integer status;

    public static FormListQuery of(Integer pageNum, Integer pageSize) {
        return FormListQuery.builder().pageNum(pageNum).pageSize(pageSize).build();
    }
}
