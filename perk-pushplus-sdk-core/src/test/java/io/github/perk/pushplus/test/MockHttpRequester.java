package io.github.perk.pushplus.test;

import io.github.perk.pushplus.http.HttpRequester;
import io.github.perk.pushplus.http.HttpResponse;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 用于单元测试的 mock HttpRequester：
 * - 支持按 path 注册响应
 * - 记录每次请求的 method/url/body/headers
 */
@Getter
public class MockHttpRequester implements HttpRequester {

    public record Recorded(String method, String url, Map<String, String> headers, String body) {}

    private final Map<String, Deque<HttpResponse>> responses = new LinkedHashMap<>();
    private final Deque<HttpResponse> defaultResponses = new ArrayDeque<>();
    private final java.util.List<Recorded> records = new java.util.ArrayList<>();

    public MockHttpRequester whenPath(String pathFragment, int status, String body) {
        responses.computeIfAbsent(pathFragment, k -> new ArrayDeque<>())
                 .addLast(new HttpResponse(status, body));
        return this;
    }

    public MockHttpRequester defaultResponse(int status, String body) {
        defaultResponses.addLast(new HttpResponse(status, body));
        return this;
    }

    @Override
    public HttpResponse execute(String method, String url, Map<String, String> headers, String body) {
        records.add(new Recorded(method, url, headers == null ? Map.of() : Map.copyOf(headers), body));
        for (var e : responses.entrySet()) {
            if (url.contains(e.getKey()) && !e.getValue().isEmpty()) {
                return e.getValue().pollFirst();
            }
        }
        if (!defaultResponses.isEmpty()) {
            return defaultResponses.pollFirst();
        }
        return new HttpResponse(200, "{\"code\":200,\"msg\":\"ok\"}");
    }
}
