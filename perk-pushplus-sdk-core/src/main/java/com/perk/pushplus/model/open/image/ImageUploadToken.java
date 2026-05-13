package com.perk.pushplus.model.open.image;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片服务 - 获取上传凭证响应。
 *
 * <p>对应文档「十二. 图片服务接口 / 1. 获取上传凭证」。
 * 返回七牛云表单上传所需的 token 及上传域名、存储桶等信息。</p>
 */
@Data
@NoArgsConstructor
public class ImageUploadToken {

    /** 七牛云上传凭证。 */
    private String uploadToken;

    /** 七牛云上传域名，例如 {@code https://upload.qiniup.com}。 */
    private String uploadHost;

    /** 七牛云上传地址，一般等同于 {@code uploadHost + "/"}。 */
    private String uploadUrl;

    /** 七牛云存储桶名称。 */
    private String bucket;

    /** 凭证有效时间（秒）。 */
    private Integer expiresIn;
}
