package com.perk.pushplus.model.open.doc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文档 / 表格分页查询。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocListQuery {

    private Integer pageNum;
    private Integer pageSize;
    private String keyword;
    /** true 时仅返回已开启分享的记录。 */
    private Boolean shareEnabled;

    public static DocListQuery of(Integer pageNum, Integer pageSize) {
        return DocListQuery.builder().pageNum(pageNum).pageSize(pageSize).build();
    }
}
