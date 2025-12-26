package io.github.motoryang.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@Data
@EqualsAndHashCode(callSuper = true)
public class ServiceException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 错误码 */
    private Integer code;

    /** 错误提示 */
    private String message;

    /** 错误明细 */
    private String detailMessage;

    public ServiceException() {
    }

    public ServiceException(String message) {
        this.message = message;
    }

    public ServiceException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ServiceException(String message, String detailMessage) {
        this.message = message;
        this.detailMessage = detailMessage;
    }

    public static ServiceException of(String message) {
        throw new ServiceException(message);
    }
}