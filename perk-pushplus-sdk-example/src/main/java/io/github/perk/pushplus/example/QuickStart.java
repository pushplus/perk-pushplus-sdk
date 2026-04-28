package io.github.perk.pushplus.example;

import io.github.perk.pushplus.PushPlusClient;
import io.github.perk.pushplus.enums.Channel;
import io.github.perk.pushplus.enums.Template;
import io.github.perk.pushplus.model.send.BatchSendRequest;
import io.github.perk.pushplus.model.send.SendRequest;

import java.util.List;

/**
 * PushPlus SDK 快速开始示例。
 *
 * <p>运行前请将 token 与 secretKey 替换为你自己的值，并确保所在 IP 已加入 PushPlus 安全 IP 列表。</p>
 */
public class QuickStart {

    public static void main(String[] args) {
        String token = System.getenv().getOrDefault("PUSHPLUS_TOKEN", "your_token");
        String secretKey = System.getenv().getOrDefault("PUSHPLUS_SECRET_KEY", "your_secret_key");

        PushPlusClient client = PushPlusClient.builder()
                .token(token)
                .secretKey(secretKey)
                .logRequest(true)
                .build();

        // 1) 简单发送
        String shortCode = client.sendSimple("SDK 测试", "Hello PushPlus");
        System.out.println("send shortCode = " + shortCode);

        // 2) 使用 Builder 发送 markdown 消息到群组
        String mdShortCode = client.send(SendRequest.builder()
                .title("部署完成")
                .content("# 项目 X 部署完成\n- 版本: v1.0.0\n- 环境: prod")
                .template(Template.MARKDOWN)
                .topic("ops") // 群组编码
                .build());
        System.out.println("markdown shortCode = " + mdShortCode);

        // 3) 多渠道发送
        var batch = client.batchSend(BatchSendRequest.builder()
                .title("多渠道告警")
                .content("CPU > 90%")
                .template(Template.HTML)
                .channel(Channel.WECHAT).option("")
                .channel(Channel.WEBHOOK).option("bark")
                .channel(Channel.EXTENSION).option("")
                .build());
        batch.forEach(r -> System.out.println(r.getChannel() + " -> " + r.getShortCode()));

        // 4) 查询消息发送结果
        var result = client.getOpenMessage().queryResult(shortCode);
        System.out.println("status = " + result.getStatusEnum() + ", msg = " + result.getErrorMessage());

        // 5) 调用开放接口
        System.out.println("个人资料: " + client.getUser().myInfo());
        System.out.println("当日发送计数: " + client.getUser().getSendCount());
    }
}
