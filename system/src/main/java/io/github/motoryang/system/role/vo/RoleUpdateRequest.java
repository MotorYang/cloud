package io.github.motoryang.system.role.vo;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 修改角色请求
 */
@Data
public class RoleUpdateRequest {

    /**
     * 角色ID
     */
    @NotBlank(message = "角色ID不能为空")
    private String roleId;

    /**
     * 角色名称
     */
    @Size(max = 30, message = "角色名称长度不能超过30个字符")
    private String roleName;

    /**
     * 角色权限字符串
     */
    @Size(max = 100, message = "权限字符长度不能超过100个字符")
    private String roleKey;

    /**
     * 显示顺序
     */
    private Integer roleSort;

    /**
     * 状态
     */
    private String status;

    /**
     * 菜单ID列表
     */
    private List<String> menuIds;

    /**
     * 备注
     */
    private String remark;
}