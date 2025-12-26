package io.github.motoryang.blog.settings.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.motoryang.blog.settings.entity.Settings;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SettingsMapper extends BaseMapper<Settings> {

    @Select(value = "select value from blog_settings where key = #{key}")
    String getValueByKey(@Param("key") String key);

    @Update(value = "update blog_settings set value = #{value} where key = #{key}")
    void updateByKey(@Param("key") String key, @Param("value") String value);
}
