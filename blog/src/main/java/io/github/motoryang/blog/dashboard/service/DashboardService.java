package io.github.motoryang.blog.dashboard.service;

import io.github.motoryang.blog.dashboard.dto.DashboardDTO;
import io.github.motoryang.blog.dashboard.dto.HotArticlesDTO;
import io.github.motoryang.blog.dashboard.dto.MonthlyGrowthDTO;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("dashboardService")
public class DashboardService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public DashboardDTO getDashbord() {

        DashboardDTO stats = new DashboardDTO();

        // 总文章数
        stats.setTotalArticles(jdbcTemplate.queryForObject("SELECT COUNT(*) FROM blog_articles", Long.class));

        // 总浏览量
        stats.setTotalViews(jdbcTemplate.queryForObject("SELECT COALESCE(SUM(views), 0) FROM blog_articles", Long.class));

        // 分类统计
        String categorySql = "SELECT category, COUNT(*) as count FROM blog_articles GROUP BY category";
        Map<String, Long> categoryMap = jdbcTemplate.query(categorySql, rs -> {
            Map<String, Long> map = new HashMap<>();
            while (rs.next()) {
                map.put(rs.getString("category"), rs.getLong("count"));
            }
            return map;
        });
        stats.setCategoryCounts(categoryMap);

        // 月度增长
        String monthlySql = "SELECT TO_CHAR(DATE_TRUNC('month', date), 'YYYY-MM') as date, " +
                "COUNT(*)::integer as count " +
                "FROM blog_articles " +
                "WHERE date >= CURRENT_DATE - INTERVAL '12 months' " +
                "GROUP BY DATE_TRUNC('month', date) " +
                "ORDER BY date DESC";
        List<MonthlyGrowthDTO> monthlyGrowth = jdbcTemplate.query(monthlySql,
                (rs, rowNum) -> {
                    MonthlyGrowthDTO dto = new MonthlyGrowthDTO();
                    dto.setMonth(rs.getString("date"));
                    dto.setCount(rs.getInt("count"));
                    return dto;
                }
        );
        stats.setMonthlyGrowth(monthlyGrowth);

        // 阅读量前十
        String hotArticlesSql = "SELECT id, title, category, views FROM blog_articles ORDER BY views DESC LIMIT 10";
        List<HotArticlesDTO> hotArticles = jdbcTemplate.query(hotArticlesSql, (rs, rowNum) -> {
           HotArticlesDTO hotArticlesDTO = new HotArticlesDTO();
           hotArticlesDTO.setId(rs.getString("id"));
           hotArticlesDTO.setTitle(rs.getString("title"));
           hotArticlesDTO.setCategory(rs.getString("category"));
           hotArticlesDTO.setViews(rs.getLong("views"));
           return hotArticlesDTO;
        });
        stats.setHotArticles(hotArticles);

        return stats;
    }

}
