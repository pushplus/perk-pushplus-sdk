package com.perk.pushplus.model.open.image;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片服务 - 图片列表项。
 *
 * <p>对应文档「十二. 图片服务接口 / 3. 图片列表」中 {@code list[]} 元素。</p>
 */
@Data
@NoArgsConstructor
public class ImageItem {

    /** 图片 id。 */
    private Long id;

    /** 图片地址。 */
    private String imgUrl;

    /** 缩略图地址。 */
    private String thumbnail;

    /** 创建时间。 */
    private String createTime;
}
