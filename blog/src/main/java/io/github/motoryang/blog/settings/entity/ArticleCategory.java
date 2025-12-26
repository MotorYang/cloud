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
@TableName(value = "blog_article_categories")
public class ArticleCategory {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    private String code;
    private String nameZh;
    private String nameEn;
    private String remark;
    private Integer count;
    private Integer sortOrder;
    private Integer articleCount;
    private Boolean isActive;

}
