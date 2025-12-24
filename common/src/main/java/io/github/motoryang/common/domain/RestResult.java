package io.github.motoryang.common.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class RestResult<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Integer code;
    private String msg;
    private T data;
    private Long timestamp;

    public RestResult() {
        this.timestamp = System.currentTimeMillis();
    }

    public RestResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> RestResult<T> success() {
        return new RestResult<>(200, "操作成功", null);
    }

    public static <T> RestResult<T> success(T data) {
        return new RestResult<>(200, "操作成功", data);
    }

    public static <T> RestResult<T> success(String msg, T data) {
        return new RestResult<>(200, msg, data);
    }

    public static <T> RestResult<T> error() {
        return new RestResult<>(500, "操作失败", null);
    }

    public static <T> RestResult<T> error(String msg) {
        return new RestResult<>(500, msg, null);
    }

    public static <T> RestResult<T> error(Integer code, String msg) {
        return new RestResult<>(code, msg, null);
    }

}
