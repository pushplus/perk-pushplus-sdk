package com.perk.pushplus.model.open.channel;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MailDetail {
    private Long id;
    private String mailName;
    private String mailCode;
    private String account;
    private String password;
    private String smtpServer;
    private Integer smtpSsl;
    private Integer smtpPort;
    private String createTime;
}
