package io.github.motoryang.blog.settings.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.motoryang.blog.settings.entity.MusicTrack;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MusicTrackMapper extends BaseMapper<MusicTrack> {

    @Select("select * from blog_music_track where is_active = true")
    List<MusicTrack> selectAllActive();

    @Delete("delete from blog_music_tracks where 1=1")
    int deleteAll();

    @Delete("""
        <script>
        DELETE FROM blog_music_track
        WHERE id NOT IN
        <foreach collection="ids" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
        </script>
        """)
    int deleteNotInIds(@Param("ids") List<String> ids);
}
