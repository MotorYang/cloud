package io.github.motoryang.blog.dashboard.dto;

import lombok.Data;

@Data
public class HotArticlesDTO {

    private String id;
    private String title;
    private String category;
    private Long views;

}
