package io.github.motoryang.system.user.vo;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 新增用户请求
 */
@Data
public class UserAddRequest {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度为2-20个字符")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度为6-20个字符")
    private String password;

    /**
     * 昵称
     */
    @Size(max = 30, message = "昵称长度不能超过30个字符")
    private String nickName;

    /**
     * 用户类型
     */
    private String userType;

    /**
     * 手机号
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 性别（0男 1女 2未知）
     */
    private String sex;

    /**
     * 角色ID列表
     */
    private List<String> roleIds;

    /**
     * 备注
     */
    private String remark;
}