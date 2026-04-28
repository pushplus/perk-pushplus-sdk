package io.github.perk.pushplus.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PushPlus 接口统一响应。
 *
 * <p>所有接口都遵循 {@code {code, msg, data}} 结构。</p>
 *
 * @param <T> data 的类型
 */
@Data
@NoArgsConstructor
public class ApiResponse<T> {

    /** 业务状态码。200 表示成功；其他为失败。 */
    private Integer code;

    /** 接口响应消息。 */
    private String msg;

    /** 业务数据。 */
    private T data;

    public boolean isSuccess() {
        return code != null && code == 200;
    }
}
