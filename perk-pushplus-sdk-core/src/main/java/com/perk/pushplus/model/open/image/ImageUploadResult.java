package com.perk.pushplus.model.open.image;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片服务 - 上传图片响应（七牛云返回）。
 *
 * <p>对应文档「十二. 图片服务接口 / 2. 上传图片」。
 * 注意：该响应由七牛云直接返回，不是 PushPlus 统一的 {@code {code, msg, data}} 结构。</p>
 */
@Data
@NoArgsConstructor
public class ImageUploadResult {

    /** 错误码；0 表示成功。 */
    private Integer errno;

    /** 文件扩展名，例如 {@code .png}。 */
    private String ext;

    /** 文件名。 */
    private String fname;

    /** 文件大小（字节）。 */
    private Long fsize;

    /** 七牛云文件 hash。 */
    private String hash;

    /** 对象存储中的路径 key。 */
    private String key;

    /** MIME 类型，例如 {@code image/png}。 */
    private String mimeType;

    /** 响应说明。 */
    private String msg;

    /** 缩略图地址。 */
    private String thumbnail;

    /** 图片访问地址。 */
    private String url;

    /** 是否上传成功（{@link #errno} 为 0）。 */
    public boolean isSuccess() {
        return errno != null && errno == 0;
    }
}
