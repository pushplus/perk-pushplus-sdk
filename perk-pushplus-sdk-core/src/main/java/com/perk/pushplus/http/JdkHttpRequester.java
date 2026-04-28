package com.perk.pushplus.http;

import com.perk.pushplus.config.PushPlusConfig;
import com.perk.pushplus.exception.PushPlusException;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

/**
 * 基于 JDK 11+ {@link HttpClient} 的请求执行器。
 *
 * <p>线程安全，可作为 SDK 单例长期复用。</p>
 */
@Slf4j
public class JdkHttpRequester implements HttpRequester {

    private final HttpClient httpClient;
    private final Duration readTimeout;
    private final boolean logRequest;

    public JdkHttpRequester(PushPlusConfig config) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(config.getConnectTimeout())
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        this.readTimeout = config.getReadTimeout();
        this.logRequest = config.isLogRequest();
    }

    @Override
    public HttpResponse execute(String method, String url, Map<String, String> headers, String body) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(readTimeout);

        boolean hasContentType = false;
        if (headers != null) {
            for (Map.Entry<String, String> e : headers.entrySet()) {
                if (e.getKey() == null || e.getValue() == null) {
                    continue;
                }
                builder.header(e.getKey(), e.getValue());
                if ("Content-Type".equalsIgnoreCase(e.getKey())) {
                    hasContentType = true;
                }
            }
        }

        HttpRequest.BodyPublisher bodyPublisher = body == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8);

        if (body != null && !hasContentType) {
            builder.header("Content-Type", "application/json;charset=UTF-8");
        }

        switch (method.toUpperCase()) {
            case "GET" -> builder.GET();
            case "POST" -> builder.POST(bodyPublisher);
            case "PUT" -> builder.PUT(bodyPublisher);
            case "DELETE" -> {
                if (body == null) {
                    builder.DELETE();
                } else {
                    builder.method("DELETE", bodyPublisher);
                }
            }
            default -> builder.method(method.toUpperCase(), bodyPublisher);
        }

        HttpRequest request = builder.build();
        if (logRequest) {
            log.debug("[pushplus] >>> {} {} body={}", method, url, body);
        }

        try {
            java.net.http.HttpResponse<String> raw = httpClient.send(request, BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (logRequest) {
                log.debug("[pushplus] <<< status={} body={}", raw.statusCode(), raw.body());
            }
            return new HttpResponse(raw.statusCode(), raw.body());
        } catch (java.io.IOException e) {
            throw new PushPlusException("调用 PushPlus 接口失败(IO异常): " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PushPlusException("调用 PushPlus 接口被中断", e);
        }
    }
}
