package io.github.motoryang.blog.dashboard.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 数据看板DTO
 */
@Data
public class DashboardDTO {

    private Long totalArticles;
    private Long totalViews;
    private Map<String, Long> categoryCounts;
    private List<MonthlyGrowthDTO> monthlyGrowth;
    private List<HotArticlesDTO> hotArticles;

}
