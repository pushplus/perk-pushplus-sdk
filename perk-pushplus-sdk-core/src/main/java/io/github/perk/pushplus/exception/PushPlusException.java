package io.github.perk.pushplus.exception;

/**
 * PushPlus SDK 统一运行时异常。
 *
 * <p>该异常会在以下场景抛出：</p>
 * <ul>
 *   <li>HTTP 请求失败（网络异常、非 2xx 状态码）</li>
 *   <li>PushPlus 业务接口返回 code != 200</li>
 *   <li>JSON 序列化/反序列化异常</li>
 *   <li>SDK 参数校验失败</li>
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
}
