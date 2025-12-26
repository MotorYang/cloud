package io.github.motoryang.blog.settings.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "blog_music_track")
public class MusicTrack {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String name;
    private String author;
    private String url;
    private Integer duration;
    private Integer sortOrder;
    private Integer playCount;
    private Boolean isActive;

}
