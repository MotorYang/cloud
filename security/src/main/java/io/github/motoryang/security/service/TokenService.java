package io.github.motoryang.security.service;

import io.github.motoryang.common.constant.Constants;
import io.github.motoryang.security.domain.LoginUser;
import io.github.motoryang.common.utils.JwtUtils;
import io.github.motoryang.common.utils.RedisCache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("tokenService")
public class TokenService {

    @Resource
    private RedisCache redisCache;

    @Resource
    private JwtUtils jwtUtils;

    /**
     * 创建访问令牌和刷新令牌
     */
    public Map<String, String> createTokens(LoginUser loginUser) {
        String username = loginUser.getUsername();
        String userId = loginUser.getUserId();

        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("userId", userId);
        claims.put("created", System.currentTimeMillis());

        // 生成访问令牌
        String accessToken = jwtUtils.createAccessToken(claims);

        // 生成刷新令牌
        String refreshToken = jwtUtils.createRefreshToken(claims);

        // 保存登录用户信息到 Redis
        String userKey = getUserKey(username);
        redisCache.setCacheObject(userKey, loginUser,
                Constants.ACCESS_TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);

        // 保存访问令牌到 Redis
        String accessKey = getAccessTokenKey(username);
        redisCache.setCacheObject(accessKey, accessToken,
                Constants.ACCESS_TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);

        // 保存刷新令牌到 Redis
        String refreshKey = getRefreshTokenKey(username);
        redisCache.setCacheObject(refreshKey, refreshToken,
                Constants.REFRESH_TOKEN_EXPIRE_TIME, TimeUnit.DAYS);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);

        return tokens;
    }

    /**
     * 获取登录用户信息
     */
    public LoginUser getLoginUser(String username) {
        String key = getUserKey(username);
        return redisCache.getCacheObject(key, LoginUser.class);
    }

    /**
     * 验证访问令牌
     */
    public boolean verifyAccessToken(String username, String token) {
        String key = getAccessTokenKey(username);
        String cachedToken = redisCache.getCacheObject(key, String.class);
        return token.equals(cachedToken);
    }

    /**
     * 验证刷新令牌
     */
    public boolean verifyRefreshToken(String username, String token) {
        String key = getRefreshTokenKey(username);
        String cachedToken = redisCache.getCacheObject(key, String.class);
        return token.equals(cachedToken);
    }

    /**
     * 刷新访问令牌
     */
    public String refreshAccessToken(String refreshToken) {
        // 验证刷新令牌格式
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new RuntimeException("刷新令牌无效");
        }

        // 从刷新令牌中获取用户信息
        String username = jwtUtils.getUsernameFromToken(refreshToken);
        String userId = jwtUtils.getUserIdFromToken(refreshToken);

        // 验证刷新令牌是否在 Redis 中
        if (!verifyRefreshToken(username, refreshToken)) {
            throw new RuntimeException("刷新令牌已失效");
        }

        // 生成新的访问令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        claims.put("userId", userId);
        claims.put("created", System.currentTimeMillis());

        String newAccessToken = jwtUtils.createAccessToken(claims);

        // 更新 Redis 中的访问令牌
        String accessKey = getAccessTokenKey(username);
        redisCache.setCacheObject(accessKey, newAccessToken,
                Constants.ACCESS_TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);

        // 刷新用户信息有效期
        String userKey = getUserKey(username);
        redisCache.expire(userKey, Constants.ACCESS_TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);

        log.info("用户 {} 刷新访问令牌成功", username);

        return newAccessToken;
    }

    /**
     * 刷新访问令牌有效期
     */
    public void refreshAccessTokenExpire(String username) {
        String accessKey = getAccessTokenKey(username);
        String userKey = getUserKey(username);
        redisCache.expire(accessKey, Constants.ACCESS_TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
        redisCache.expire(userKey, Constants.ACCESS_TOKEN_EXPIRE_TIME, TimeUnit.MINUTES);
    }

    /**
     * 删除令牌（退出登录）
     */
    public void deleteTokens(String username) {
        String accessKey = getAccessTokenKey(username);
        String refreshKey = getRefreshTokenKey(username);
        String userKey = getUserKey(username);

        redisCache.deleteObject(accessKey);
        redisCache.deleteObject(refreshKey);
        redisCache.deleteObject(userKey);

        log.info("用户 {} 退出登录", username);
    }

    /**
     * 获取用户信息的 redis key
     */
    private String getUserKey(String username) {
        return "login_user:" + username;
    }

    /**
     * 获取访问令牌的 redis key
     */
    private String getAccessTokenKey(String username) {
        return Constants.LOGIN_TOKEN_KEY + username;
    }

    /**
     * 获取刷新令牌的 redis key
     */
    private String getRefreshTokenKey(String username) {
        return Constants.REFRESH_TOKEN_KEY + username;
    }

}
