package io.github.motoryang.system.auth.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 验证码返回对象
 */
@Data
public class CaptchaVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 验证码UUID
     */
    private String captchaId;

    /**
     * 验证码图片（Base64）
     */
    private String image;
}