package io.github.motoryang.blog.settings.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class MusicTrackDTO {

    private String id;
    @NotBlank(message = "歌曲名称不能为空")
    @Size(max = 200, message = "歌曲名称不能超过200字符")
    private String name;
    @NotBlank(message = "艺术家名称不能为空")
    @Size(max = 100, message = "艺术家名称不能超过100字符")
    private String author;
    @NotBlank(message = "音频URL不能为空")
    @URL(message = "无效的URL格式")
    private String url;
    private Integer duration;
    private Integer sortOrder;

}
