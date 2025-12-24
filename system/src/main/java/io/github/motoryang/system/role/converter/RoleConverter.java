package io.github.motoryang.system.role.converter;

import io.github.motoryang.system.role.dto.RoleDTO;
import io.github.motoryang.system.role.entity.Role;
import io.github.motoryang.system.role.vo.RoleAddRequest;
import io.github.motoryang.system.role.vo.RoleUpdateRequest;
import io.github.motoryang.system.role.vo.RoleVO;
import org.mapstruct.*;

import java.util.List;

/**
 * 角色转换器
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RoleConverter {

    /**
     * Entity -> VO
     */
    @Mapping(target = "statusText", expression = "java(formatStatus(entity.getStatus()))")
    RoleVO toVO(Role entity);

    /**
     * Entity -> DTO
     */
    RoleDTO toDTO(Role entity);

    /**
     * AddRequest -> Entity
     */
    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "status", constant = "0")
    @Mapping(target = "deleted", constant = "0")
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    Role toEntity(RoleAddRequest request);

    /**
     * UpdateRequest -> Entity
     */
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    Role toEntity(RoleUpdateRequest request);

    /**
     * 更新 Entity（只更新非 null 字段）
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createBy", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    void updateEntity(RoleUpdateRequest request, @MappingTarget Role entity);

    /**
     * List<Entity> -> List<VO>
     */
    List<RoleVO> toVOList(List<Role> entities);

    /**
     * List<Entity> -> List<DTO>
     */
    List<RoleDTO> toDTOList(List<Role> entities);

    // ==================== 自定义方法 ====================

    /**
     * 格式化状态
     */
    default String formatStatus(String status) {
        return "0".equals(status) ? "正常" : "停用";
    }
}