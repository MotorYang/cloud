package io.github.motoryang.blog.settings.request;

import io.github.motoryang.blog.settings.dto.ArticleCategoryDTO;
import io.github.motoryang.blog.settings.dto.MusicTrackDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class SettingsPayload {

    @Pattern(regexp = "^AIza[A-Za-z0-9_-]{35}$", message = "无效的API密钥格式")
    private String apiKey;
    @Valid
    private List<MusicTrackDTO> musicTracks;
    @Valid
    private List<ArticleCategoryDTO> categories;

}
