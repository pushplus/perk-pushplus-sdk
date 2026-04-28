package com.perk.pushplus.enums;

/**
 * PushPlus 接口业务返回码语义。
 *
 * <p>对应官方文档「接口返回码说明」：
 * <a href="https://www.pushplus.plus/doc/guide/code.html">https://www.pushplus.plus/doc/guide/code.html</a></p>
 *
 * <p>用于在异常或日志中以更可读的方式判断错误类型，避免到处出现魔法数字。
 * 未在文档中列出的 code 会被归类为 {@link #UNKNOWN}（仍保留原始 code 值）。</p>
 */
public enum ErrorCode {

    /** 200 执行成功。 */
    OK(200),
    /** 302 未登录。 */
    NOT_LOGIN(302),
    /** 401 请求未授权（开放接口未启用）。 */
    UNAUTHORIZED(401),
    /** 403 请求 IP 未授权（白名单未配置）。 */
    IP_FORBIDDEN(403),
    /** 500 系统异常，请稍后再试。 */
    SERVER_ERROR(500),
    /** 600 数据异常，操作失败。 */
    DATA_ERROR(600),
    /** 805 无权查看。 */
    FORBIDDEN_VIEW(805),
    /** 888 积分不足，需要充值。 */
    INSUFFICIENT_POINTS(888),
    /** 900 用户账号使用受限（请求次数过多，命中后当天/数日内应停止继续请求）。 */
    RATE_LIMITED(900),
    /** 905 账户未进行实名认证。 */
    NOT_VERIFIED(905),
    /** 903 无效的用户令牌（token 不正确）。 */
    INVALID_TOKEN(903),
    /** 999 服务端验证错误。 */
    VALIDATION_ERROR(999),
    /** 其它未在文档中列出的 code。 */
    UNKNOWN(-1);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /**
     * 把数值 code 映射为枚举；未匹配时返回 {@link #UNKNOWN}。
     */
    public static ErrorCode fromCode(Integer code) {
        if (code == null) {
            return UNKNOWN;
        }
        for (ErrorCode ec : values()) {
            if (ec.code == code) {
                return ec;
            }
        }
        return UNKNOWN;
    }

    /** 是否被 PushPlus 限流（code=900）。 */
    public static boolean isRateLimited(Integer code) {
        return code != null && code == RATE_LIMITED.code;
    }
}
