package io.github.motoryang.blog.settings.converter;

import io.github.motoryang.blog.settings.dto.MusicTrackDTO;
import io.github.motoryang.blog.settings.entity.MusicTrack;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * 音乐曲目转换器
 * 使用 MapStruct 自动生成 Entity 和 DTO 之间的转换代码
 *
 * @author motoryang
 * @since 2024-01-01
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MusicTrackConverter {

    /**
     * Entity -> DTO
     * 将数据库实体转换为数据传输对象
     *
     * @param entity 数据库实体
     * @return DTO对象
     */
    MusicTrackDTO toDTO(MusicTrack entity);

    /**
     * DTO -> Entity
     * 将数据传输对象转换为数据库实体
     *
     * @param dto DTO对象
     * @return 数据库实体
     */
    MusicTrack toEntity(MusicTrackDTO dto);

    /**
     * Entity List -> DTO List
     * 批量转换：实体列表 -> DTO列表
     *
     * @param entities 实体列表
     * @return DTO列表
     */
    List<MusicTrackDTO> toDTOList(List<MusicTrack> entities);

    /**
     * DTO List -> Entity List
     * 批量转换：DTO列表 -> 实体列表
     *
     * @param dtos DTO列表
     * @return 实体列表
     */
    List<MusicTrack> toEntityList(List<MusicTrackDTO> dtos);

    /**
     * 更新实体对象（用于更新操作）
     * 保留 ID 和创建时间，只更新其他字段
     *
     * @param dto 源数据
     * @param entity 目标实体（会被修改）
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    void updateEntity(MusicTrackDTO dto, @MappingTarget MusicTrack entity);
}