package io.github.perk.pushplus.model.open.topic;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TopicQrCode {
    private String qrCodeImgUrl;
    /** 0-临时二维码，1-永久二维码。 */
    private Integer forever;
}
