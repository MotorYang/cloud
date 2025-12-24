package io.github.motoryang.system.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wf.captcha.SpecCaptcha;
import io.github.motoryang.common.constant.Constants;
import io.github.motoryang.common.exception.ServiceException;
import io.github.motoryang.common.utils.RedisCache;
import io.github.motoryang.security.domain.LoginUser;
import io.github.motoryang.security.service.TokenService;
import io.github.motoryang.security.utils.SecurityUtils;
import io.github.motoryang.system.auth.entity.UserRole;
import io.github.motoryang.system.auth.mapper.UserRoleMapper;
import io.github.motoryang.system.auth.vo.*;
import io.github.motoryang.system.user.converter.UserConverter;
import io.github.motoryang.system.user.entity.User;
import io.github.motoryang.system.user.mapper.UserMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务
 */
@Slf4j
@Service
public class AuthService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private UserRoleMapper userRoleMapper;

    @Resource
    private TokenService tokenService;

    @Resource
    private RedisCache redisCache;

    @Resource
    private UserConverter userConverter;

    /**
     * 获取验证码
     */
    public CaptchaVO getCaptcha() {
        // 生成验证码（宽130，高48，4位数字）
        SpecCaptcha captcha = new SpecCaptcha(130, 48, 4);
        String code = captcha.text().toLowerCase();  // 转小写
        String image = captcha.toBase64();

        // 生成 UUID
        String uuid = UUID.randomUUID().toString();

        // 保存验证码到 Redis（2分钟有效）
        String key = Constants.CAPTCHA_CODE_KEY + uuid;
        redisCache.setCacheObject(key, code, Constants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);

        log.info("生成验证码，UUID: {}, Code: {}", uuid, code);

        CaptchaVO vo = new CaptchaVO();
        vo.setCaptchaId(uuid);
        vo.setImage(image);

        return vo;
    }

    /**
     * 验证验证码
     */
    private void validateCaptcha(String captchaId, String captcha) {
        if (!StringUtils.hasText(captchaId) || !StringUtils.hasText(captcha)) {
            throw new ServiceException("验证码不能为空");
        }

        String key = Constants.CAPTCHA_CODE_KEY + captchaId;
        String savedCode = redisCache.getCacheObject(key, String.class);

        if (!StringUtils.hasText(savedCode)) {
            throw new ServiceException("验证码已过期，请重新获取");
        }

        // 不区分大小写比较
        if (!captcha.equalsIgnoreCase(savedCode)) {
            throw new ServiceException("验证码错误");
        }

        // 验证成功后删除验证码（一次性使用）
        redisCache.deleteObject(key);
    }

    /**
     * 登录
     */
    public LoginResponse login(LoginRequest request) {
        // 1. 验证验证码
        validateCaptcha(request.getCaptchaId(), request.getCaptcha());

        // 2. 查询用户
        User user = userMapper.selectUserByUsername(request.getUsername());
        if (user == null) {
            throw new ServiceException("用户不存在");
        }

        // 3. 验证密码
        if (!SecurityUtils.matchesPassword(request.getPassword(), user.getPassword())) {
            log.warn("用户 {} 登录失败：密码错误", request.getUsername());
            throw new ServiceException("密码错误");
        }

        // 4. 检查用户状态
        if ("1".equals(user.getStatus())) {
            throw new ServiceException("账号已被停用，请联系管理员");
        }

        // 5. 查询用户的角色和权限
        //Set<String> roles = userMapper.selectRoleKeysByUserId(user.getUserId());
        //Set<String> permissions = userMapper.selectPermissionsByUserId(user.getUserId());

        Set<String> roles = new HashSet<>();
        roles.add("admin");

        // 6. 构建登录用户对象
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUsername(user.getUsername());
        loginUser.setNickName(user.getNickName());
        loginUser.setUserType(user.getUserType());
        loginUser.setPhone(user.getPhone());
        loginUser.setEmail(user.getEmail());
        loginUser.setStatus(user.getStatus());
        loginUser.setRoles(roles);
        loginUser.setPermissions(new HashSet<>());

        // 7. 生成 Token
        Map<String, String> tokens = tokenService.createTokens(loginUser);

        // 8. 构建返回对象
        LoginResponse vo = new LoginResponse();
        vo.setAccessToken(tokens.get("accessToken"));
        vo.setRefreshToken(tokens.get("refreshToken"));
        vo.setTokenType("Bearer");
        vo.setExpiresIn(Constants.ACCESS_TOKEN_EXPIRE_TIME * 60);  // 秒

        // 9. 用户信息
        UserInfoVO userInfo = new UserInfoVO();
        userInfo.setUserId(user.getUserId());
        userInfo.setUsername(user.getUsername());
        userInfo.setNickName(user.getNickName());
        userInfo.setAvatar(user.getAvatar());
        userInfo.setRoles(roles);
        userInfo.setPermissions(new HashSet<>());
        vo.setUserInfo(userInfo);

        log.info("用户 {} 登录成功", request.getUsername());

        return vo;
    }

    /**
     * 注册
     */
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterRequest request) {
        // 1. 验证验证码
        validateCaptcha(request.getCaptchaId(), request.getCaptcha());

        // 2. 验证两次密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ServiceException("两次输入的密码不一致");
        }

        // 3. 检查用户名是否已存在
        LambdaQueryWrapper<User> usernameWrapper = new LambdaQueryWrapper<>();
        usernameWrapper.eq(User::getUsername, request.getUsername());
        User existUser = userMapper.selectOne(usernameWrapper);
        if (existUser != null) {
            throw new ServiceException("用户名已存在");
        }

        // 4. 检查手机号是否已存在
        LambdaQueryWrapper<User> phoneWrapper = new LambdaQueryWrapper<>();
        phoneWrapper.eq(User::getPhone, request.getPhone());
        User phoneUser = userMapper.selectOne(phoneWrapper);
        if (phoneUser != null) {
            throw new ServiceException("手机号已被注册");
        }

        // 5. 检查邮箱是否已存在
        LambdaQueryWrapper<User> emailWrapper = new LambdaQueryWrapper<>();
        emailWrapper.eq(User::getEmail, request.getEmail());
        User emailUser = userMapper.selectOne(emailWrapper);
        if (emailUser != null) {
            throw new ServiceException("邮箱已被注册");
        }

        // 6. 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(SecurityUtils.encryptPassword(request.getPassword()));
        user.setNickName(StringUtils.hasText(request.getNickName())
                ? request.getNickName()
                : request.getUsername());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setUserType("00");  // 普通用户
        user.setStatus("0");     // 正常状态
        user.setDeleted("0");    // 未删除

        int result = userMapper.insert(user);
        if (result <= 0) {
            throw new ServiceException("注册失败");
        }

        // 7. 分配默认角色（普通用户角色）
        assignDefaultRole(user.getUserId());

        log.info("用户 {} 注册成功，ID: {}", request.getUsername(), user.getUserId());
    }

    /**
     * 分配默认角色
     */
    private void assignDefaultRole(String userId) {
        // 查询默认角色ID（角色key为'user'的角色）
        // 这里简化处理，实际应该从配置或数据库读取
        String defaultRoleId = "2";  // 假设普通用户角色ID为2

        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(defaultRoleId);

        userRoleMapper.insert(userRole);
    }

    /**
     * 注销（退出登录）
     */
    public void logout() {
        String username = SecurityUtils.getUsername();

        if (!StringUtils.hasText(username)) {
            log.warn("退出登录失败：未获取到当前登录用户");
            return;
        }

        // 删除 Redis 中的 Token
        tokenService.deleteTokens(username);

        log.info("用户 {} 退出登录", username);
    }

    /**
     * 获取当前登录用户信息
     */
    public UserInfoVO getUserInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new ServiceException("未登录");
        }

        UserInfoVO vo = new UserInfoVO();
        vo.setUserId(String.valueOf(loginUser.getUserId()));
        vo.setUsername(loginUser.getUsername());
        vo.setNickName(loginUser.getNickName());

        // 查询最新的用户信息
        User user = userMapper.selectById(String.valueOf(loginUser.getUserId()));
        if (user != null) {
            vo.setAvatar(user.getAvatar());
        }

        vo.setRoles(loginUser.getRoles());
        vo.setPermissions(loginUser.getPermissions());

        return vo;
    }
}