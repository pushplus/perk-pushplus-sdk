package com.perk.pushplus.http;

import java.nio.charset.StandardCharsets;
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

    /**
     * 执行带二进制请求体的 HTTP 请求；用于 multipart 上传等场景。
     *
     * <p>默认实现会把字节按 UTF-8 转成字符串后调用 {@link #execute(String, String, Map, String)}，
     * 仅适用于 body 本身是文本的场景。涉及二进制（如图片上传）时，自定义实现应当覆写此方法以避免编码损坏。</p>
     *
     * @param method  HTTP 方法
     * @param url     完整请求 URL
     * @param headers 请求头（可为 null）
     * @param body    请求体字节；为 null 表示不带 body
     * @return 响应
     */
    default HttpResponse executeRaw(String method, String url, Map<String, String> headers, byte[] body) {
        String text = body == null ? null : new String(body, StandardCharsets.UTF_8);
        return execute(method, url, headers, text);
    }
}
