package io.github.motoryang.blog.dashboard.dto;

import lombok.Data;

/**
 * 文章阅读增长DTO
 */
@Data
public class MonthlyGrowthDTO {

    private String month;
    private Integer count;

}
