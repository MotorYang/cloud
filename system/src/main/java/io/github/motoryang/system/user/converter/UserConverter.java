package io.github.motoryang.system.user.converter;

import io.github.motoryang.security.utils.SecurityUtils;
import io.github.motoryang.system.user.dto.UserDTO;
import io.github.motoryang.system.user.entity.User;
import io.github.motoryang.system.user.vo.UserAddRequest;
import io.github.motoryang.system.user.vo.UserUpdateRequest;
import io.github.motoryang.system.user.vo.UserVO;
import org.mapstruct.*;

import java.util.List;

/**
 * 用户转换器
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface UserConverter {

    /**
     * Entity -> VO
     */
    @Mapping(target = "sexText", source = "sex", qualifiedByName = "formatSex")  // 指定使用 formatSex
    @Mapping(target = "statusText", source = "status", qualifiedByName = "formatStatus")  // 指定使用 formatStatus
    @Mapping(target = "roles", ignore = true)
    UserVO toVO(User entity);

    /**
     * Entity -> DTO
     */
    UserDTO toDTO(User entity);

    /**
     * DTO -> VO
     */
    @Mapping(target = "sexText", source = "sex", qualifiedByName = "formatSex")  // 指定使用 formatSex
    @Mapping(target = "statusText", source = "status", qualifiedByName = "formatStatus")  // 指定使用 formatStatus
    @Mapping(target = "roles", ignore = true)
    UserVO toVO(UserDTO userDTO);

    /**
     * AddRequest -> Entity
     */
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", source = "password", qualifiedByName = "encryptPassword")  // 指定使用 encryptPassword
    @Mapping(target = "status", constant = "0")
    @Mapping(target = "deleted", constant = "0")
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    User toEntity(UserAddRequest request);

    /**
     * UpdateRequest -> Entity
     */
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    User toEntity(UserUpdateRequest request);

    /**
     * 更新 Entity（只更新非 null 字段）
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void updateEntity(UserUpdateRequest request, @MappingTarget User entity);

    /**
     * List<Entity> -> List<VO>
     */
    List<UserVO> toVOList(List<User> entities);

    /**
     * List<Entity> -> List<DTO>
     */
    List<UserDTO> toDTOList(List<User> entities);

    // ==================== 自定义方法（使用 @Named 标记） ====================

    /**
     * 格式化性别
     */
    @Named("formatSex")  // 添加 @Named 注解
    default String formatSex(String sex) {
        if (sex == null) {
            return "未知";
        }
        switch (sex) {
            case "0":
                return "男";
            case "1":
                return "女";
            default:
                return "未知";
        }
    }

    /**
     * 格式化状态
     */
    @Named("formatStatus")  // 添加 @Named 注解
    default String formatStatus(String status) {
        return "0".equals(status) ? "正常" : "停用";
    }

    /**
     * 加密密码
     */
    @Named("encryptPassword")  // 添加 @Named 注解
    default String encryptPassword(String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }
        return SecurityUtils.encryptPassword(password);
    }
}