package io.github.motoryang.blog.settings.dto;

import lombok.Data;

@Data
public class ArticleCategoryDTO {

    private String id;
    private String code;
    private String nameZh;
    private String nameEn;
    private String remark;
    private Integer count;
    private Integer sortOrder;

}
