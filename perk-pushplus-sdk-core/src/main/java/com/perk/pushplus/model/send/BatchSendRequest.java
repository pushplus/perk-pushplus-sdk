package com.perk.pushplus.model.send;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.perk.pushplus.enums.Channel;
import com.perk.pushplus.enums.Template;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 多渠道发送消息请求。
 *
 * <p>对应 {@code /batchSend} 接口。Builder 提供 {@link Builder#channel(Channel)} 与
 * {@link Builder#option(String)} 的便捷追加方式：内部自动以逗号拼接，按顺序一一对应。</p>
 *
 * <pre>{@code
 * BatchSendRequest req = BatchSendRequest.builder()
 *     .title("多渠道告警")
 *     .content("...")
 *     .channel(Channel.WECHAT).option("")
 *     .channel(Channel.WEBHOOK).option("bark")
 *     .channel(Channel.EXTENSION).option("")
 *     .build();
 * }</pre>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchSendRequest {

    private String token;
    private String title;
    private String content;
    private String topic;
    private Template template;

    /** 多渠道，逗号分隔。 */
    private String channel;

    /** 多渠道 option，逗号分隔；与 channel 一一对应。 */
    private String option;

    private String callbackUrl;
    private Long timestamp;
    private String to;
    private String pre;

    /** 复制当前对象的内容到一个新的 Builder（用于修改后再次 build）。 */
    public Builder toBuilder() {
        return new Builder()
                .token(token).title(title).content(content).topic(topic).template(template)
                .channelString(channel).optionString(option)
                .callbackUrl(callbackUrl).timestamp(timestamp).to(to).pre(pre);
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * 链式 Builder，支持以 {@code channel(Channel)} / {@code option(String)} 形式累积调用，
     * 内部自动用逗号拼接并按顺序一一对应。
     *
     * <p>也可以通过 {@link #channelString(String)} / {@link #optionString(String)} 直接指定 CSV 字符串。
     * 两种风格不要混用：累积式优先级高于直接 CSV。</p>
     */
    public static class Builder {
        private String token;
        private String title;
        private String content;
        private String topic;
        private Template template;
        private String channel;
        private String option;
        private String callbackUrl;
        private Long timestamp;
        private String to;
        private String pre;

        private final List<Channel> channelList = new ArrayList<>();
        private final List<String> optionList = new ArrayList<>();

        public Builder token(String v) { this.token = v; return this; }
        public Builder title(String v) { this.title = v; return this; }
        public Builder content(String v) { this.content = v; return this; }
        public Builder topic(String v) { this.topic = v; return this; }
        public Builder template(Template v) { this.template = v; return this; }
        public Builder callbackUrl(String v) { this.callbackUrl = v; return this; }
        public Builder timestamp(Long v) { this.timestamp = v; return this; }
        public Builder to(String v) { this.to = v; return this; }
        public Builder pre(String v) { this.pre = v; return this; }

        /** 追加一个 channel。 */
        public Builder channel(Channel ch) { channelList.add(ch); return this; }

        /** 追加一个 option，与最近一次 channel() 配对；可传空串。 */
        public Builder option(String opt) { optionList.add(opt == null ? "" : opt); return this; }

        /** 直接以 CSV 形式指定多渠道字符串（与 {@link #channel(Channel)} 互斥）。 */
        public Builder channelString(String csv) { this.channel = csv; return this; }

        /** 直接以 CSV 形式指定 option 字符串。 */
        public Builder optionString(String csv) { this.option = csv; return this; }

        public BatchSendRequest build() {
            String finalChannel = !channelList.isEmpty()
                    ? channelList.stream().map(Channel::getCode).collect(Collectors.joining(","))
                    : channel;
            String finalOption = !optionList.isEmpty()
                    ? String.join(",", optionList)
                    : option;
            return new BatchSendRequest(token, title, content, topic, template,
                    finalChannel, finalOption, callbackUrl, timestamp, to, pre);
        }
    }
}
