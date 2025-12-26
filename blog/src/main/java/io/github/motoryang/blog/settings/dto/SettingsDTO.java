package io.github.motoryang.blog.settings.dto;

import lombok.Data;

import java.util.List;

@Data
public class SettingsDTO {

    private String apiKey;
    private List<MusicTrackDTO> musicTracks;
    private List<ArticleCategoryDTO> categories;

}
