package io.github.motoryang.blog.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * CryptoUtils - AES 加解密工具（CBC + PKCS5Padding + 随机 IV）
 * 兼容 Hex 存储，支持 Nacos 热更新
 */
@Component
@RefreshScope
public class CryptoUtils {

    private final byte[] key;

    public CryptoUtils(@Value("${security.aes-secret}") String aesKey) {
        if (!StringUtils.hasText(aesKey)) {
            throw new IllegalArgumentException("AES 密钥未配置");
        }
        this.key = aesKey.getBytes();
    }

    /**
     * 加密
     * @param plainText 明文
     * @return Hex 编码密文
     */
    public String encrypt(String plainText) {
        if (plainText == null) {
            return null;
        }
        // AES 默认 CBC + PKCS5Padding + 随机 IV
        AES aes = SecureUtil.aes(key);
        return aes.encryptHex(plainText);
    }

    /**
     * 解密
     * @param cipherText Hex 编码密文
     * @return 明文
     */
    public String decrypt(String cipherText) {
        if (!StringUtils.hasText(cipherText)) {
            return null;
        }
        AES aes = SecureUtil.aes(key);
        return aes.decryptStr(cipherText);
    }
}