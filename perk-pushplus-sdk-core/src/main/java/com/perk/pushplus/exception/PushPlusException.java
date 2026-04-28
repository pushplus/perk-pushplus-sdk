package com.perk.pushplus.exception;

import com.perk.pushplus.enums.ErrorCode;

/**
 * PushPlus SDK 统一运行时异常。
 *
 * <p>该异常会在以下场景抛出：</p>
 * <ul>
 *   <li>HTTP 请求失败（网络异常、非 2xx 状态码）</li>
 *   <li>PushPlus 业务接口返回 code != 200</li>
 *   <li>JSON 序列化/反序列化异常</li>
 *   <li>SDK 参数校验失败</li>
 *   <li>本地限流守卫命中（code=900 后被短路），不会真正发起 HTTP 请求</li>
 * </ul>
 */
public class PushPlusException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /** PushPlus 接口返回的业务 code。HTTP 错误时为对应的 HTTP 状态码；其他为 -1。 */
    private final int code;

    public PushPlusException(String message) {
        this(-1, message, null);
    }

    public PushPlusException(String message, Throwable cause) {
        this(-1, message, cause);
    }

    public PushPlusException(int code, String message) {
        this(code, message, null);
    }

    public PushPlusException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /** 把数值 code 映射为 {@link ErrorCode} 枚举（未知为 {@link ErrorCode#UNKNOWN}）。 */
    public ErrorCode getErrorCode() {
        return ErrorCode.fromCode(code);
    }

    /** 是否为 PushPlus 限流（code=900）。命中后建议当天停止继续调用发送消息接口。 */
    public boolean isRateLimited() {
        return ErrorCode.isRateLimited(code);
    }
}
