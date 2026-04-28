package com.perk.pushplus.spring;

import com.perk.pushplus.PushPlusClient;
import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.http.HttpRequester;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PushPlus Spring Boot 自动装配。
 *
 * <p>引入本 starter 后，配置 {@code pushplus.token=xxx}（以及可选的 {@code pushplus.secret-key=xxx}），
 * 即可在 Spring 容器中自动获得 {@link PushPlusClient} bean。</p>
 *
 * <pre>{@code
 * @Service
 * public class NotifyService {
 *     private final PushPlusClient pushPlus;
 *     public NotifyService(PushPlusClient pushPlus) { this.pushPlus = pushPlus; }
 *
 *     public void notify(String msg) {
 *         pushPlus.sendSimple("系统通知", msg);
 *     }
 * }
 * }</pre>
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(PushPlusClient.class)
@ConditionalOnProperty(prefix = "pushplus", name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(PushPlusProperties.class)
public class PushPlusAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public PushPlusConfig pushPlusConfig(PushPlusProperties properties) {
        return properties.toConfig();
    }

    @Bean
    @ConditionalOnMissingBean
    public PushPlusClient pushPlusClient(PushPlusConfig config,
                                         org.springframework.beans.factory.ObjectProvider<HttpRequester> httpRequester) {
        HttpRequester custom = httpRequester.getIfAvailable();
        return PushPlusClient.builder().config(config).httpRequester(custom).build();
    }
}
