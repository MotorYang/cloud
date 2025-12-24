package io.github.motoryang.blog.article.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.motoryang.blog.article.dto.ArticleCreateDTO;
import io.github.motoryang.blog.article.dto.ArticleDTO;
import io.github.motoryang.blog.article.dto.ArticleFilterDTO;
import io.github.motoryang.blog.article.entity.Article;
import io.github.motoryang.blog.article.service.ArticleServiceOptimized;
import io.github.motoryang.common.domain.RestResult;
import io.github.motoryang.common.query.PageRequest;
import io.github.motoryang.common.query.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Article REST API 控制器
 */
@RestController
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleServiceOptimized articleService;

    @PostMapping("/query")
    public RestResult<PageResult<ArticleDTO>> query(@RequestBody PageRequest<ArticleFilterDTO> pageRequest) {
        Page<Article> page = new Page<>(pageRequest.getPage(), pageRequest.getSize());
        PageResult<ArticleDTO> pageResult = articleService.queryArticle(page, pageRequest.getFilter());
        return RestResult.success(pageResult);
    }

    /**
     * 获取所有文章
     * GET /articles
     */
    @GetMapping("/all")
    public RestResult<List<ArticleDTO>> getAllArticles() {
        List<ArticleDTO> articles = articleService.getAllArticles();
        return RestResult.success(articles);
    }

    /**
     * 根据ID获取文章
     * GET /articles/{id}
     */
    @GetMapping("/get/{id}")
    public RestResult<ArticleDTO> getArticleById(@PathVariable("id") String id) {
        return articleService.getArticleById(id)
                .map(RestResult::success)
                .orElse(RestResult.error(11002, "文章不存在！"));
    }

    /**
     * 创建新文章
     * POST /articles
     */
    @PostMapping("/create")
    public RestResult<ArticleDTO> createArticle(@RequestBody ArticleCreateDTO createDTO) {
        ArticleDTO createdArticle = articleService.createArticle(createDTO);
        return RestResult.success(createdArticle);
    }

    /**
     * 删除文章
     * DELETE /articles/{id}
     */
    @DeleteMapping("/del/{id}")
    public RestResult<Void> deleteArticle(@PathVariable("id") String id) {
        boolean deleted = articleService.deleteArticle(id);
        if (deleted) {
            return RestResult.success(null);
        }
        return RestResult.error(11002, "文章不存在!");
    }

    /**
     * 更新文章
     * PUT /articles/update/{id}
     */
    @PutMapping("/update")
    public RestResult<Integer> updateArticle(@RequestBody ArticleCreateDTO updateDTO) {
        int result = articleService.updateArticle(updateDTO);
        if (result == 0) {
            return RestResult.error("未更新任何内容！");
        }
        return RestResult.success(1);
    }

    /**
     * 增加文章浏览量
     * POST /articles/{id}/increment-views
     */
    @GetMapping("/views/{id}")
    public RestResult<Integer> incrementViews(@PathVariable("id") String id) {
        Integer views = articleService.incrementViews(id);
        return RestResult.success(views);
    }

    /**
     * 按分类获取文章
     * GET /articles/category/{category}
     */
    @GetMapping("/category/{category}")
    public RestResult<List<ArticleDTO>> getArticlesByCategory(@PathVariable("category") String category) {
        List<ArticleDTO> articles = articleService.getArticlesByCategory(category);
        return RestResult.success(articles);
    }

    /**
     * 按作者获取文章
     * GET /articles/author/{author}
     */
    @GetMapping("/author/{author}")
    public RestResult<List<ArticleDTO>> getArticlesByAuthor(@PathVariable("author") String author) {
        List<ArticleDTO> articles = articleService.getArticlesByAuthor(author);
        return RestResult.success(articles);
    }

    /**
     * 搜索文章
     * GET /articles/search?keyword={keyword}
     */
    @GetMapping("/search")
    public RestResult<List<ArticleDTO>> searchArticles(@RequestParam String keyword) {
        List<ArticleDTO> articles = articleService.searchArticles(keyword);
        return RestResult.success(articles);
    }
}