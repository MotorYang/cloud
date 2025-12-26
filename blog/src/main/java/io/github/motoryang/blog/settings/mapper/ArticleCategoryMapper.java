package io.github.motoryang.blog.settings.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.github.motoryang.blog.settings.entity.ArticleCategory;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ArticleCategoryMapper extends BaseMapper<ArticleCategory> {

    @Select("select * from blog_article_categories where is_active = true;")
    List<ArticleCategory> selectAllActive();

    @Delete("delete from blog_article_categories where 1=1")
    int deleteAll();

    @Delete("""
        <script>
        DELETE FROM blog_article_categories
        WHERE id NOT IN
        <foreach collection="ids" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
        </script>
        """)
    int deleteNotInIds(@Param("ids") List<String> ids);

}
