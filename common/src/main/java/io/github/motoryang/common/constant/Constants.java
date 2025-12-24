package io.github.motoryang.common.constant;

public class Constants {

    /** UTF-8 字符集 */
    public static final String UTF8 = "UTF-8";

    /** 成功标记 */
    public static final Integer SUCCESS = 200;

    /** 失败标记 */
    public static final Integer FAIL = 500;

    /** 登录用户 redis key */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /** 刷新令牌 redis key */
    public static final String REFRESH_TOKEN_KEY = "refresh_tokens:";

    /** 验证码 redis key */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /** 验证码有效期（分钟） */
    public static final long CAPTCHA_EXPIRATION = 2;

    /** 访问令牌有效期（分钟） */
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 30;

    /** 刷新令牌有效期（天） */
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 7;

    /** 令牌前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";

    /** 令牌 header 名称 */
    public static final String HEADER_TOKEN = "Authorization";

    /** 刷新令牌 header 名称 */
    public static final String HEADER_REFRESH_TOKEN = "Refresh-Token";

    /** 内部调用标识 header */
    public static final String HEADER_INNER_FLAG = "Inner-Request";

    /** 内部调用密钥（建议配置在配置文件中） */
    public static final String INNER_SECRET = "your-inner-secret-key-123456";

}
