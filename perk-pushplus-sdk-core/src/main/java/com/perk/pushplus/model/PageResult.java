package com.perk.pushplus.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应结构。
 */
@Data
@NoArgsConstructor
public class PageResult<T> {

    private Integer pageNum;
    private Integer pageSize;
    private Integer total;
    private Integer pages;
    private List<T> list;
}
