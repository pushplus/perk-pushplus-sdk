package com.perk.pushplus.http;

import java.util.Map;

/**
 * HTTP 请求执行器抽象。
 *
 * <p>SDK 默认提供基于 JDK {@link java.net.http.HttpClient} 的实现，
 * 调用方也可以自行实现并通过 {@code PushPlusClient} 注入以使用其他客户端（如 OkHttp）。</p>
 */
public interface HttpRequester {

    /**
     * 执行 HTTP 请求。
     *
     * @param method  HTTP 方法（GET/POST/PUT/DELETE）
     * @param url     完整请求 URL
     * @param headers 请求头（可为 null）
     * @param body    请求体；为 null 表示不带 body
     * @return 响应
     */
    HttpResponse execute(String method, String url, Map<String, String> headers, String body);
}
