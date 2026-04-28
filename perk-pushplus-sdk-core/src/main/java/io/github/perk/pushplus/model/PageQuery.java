package io.github.perk.pushplus.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用分页请求参数。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageQuery {

    /** 当前所在分页数，默认 1。 */
    private Integer current;

    /** 每页大小，默认 20，最大 50。 */
    private Integer pageSize;

    public static PageQuery of(Integer current, Integer pageSize) {
        return new PageQuery(current, pageSize);
    }
}
