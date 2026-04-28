package com.perk.pushplus.http;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * HTTP 响应抽象。
 */
@Getter
@RequiredArgsConstructor
public class HttpResponse {

    private final int statusCode;
    private final String body;

    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }
}
