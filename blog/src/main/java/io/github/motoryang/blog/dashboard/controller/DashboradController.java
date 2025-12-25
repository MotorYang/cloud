package io.github.motoryang.blog.dashboard.controller;

import io.github.motoryang.blog.dashboard.dto.DashboardDTO;
import io.github.motoryang.blog.dashboard.service.DashboardService;
import io.github.motoryang.common.domain.RestResult;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboradController {

    @Resource
    private DashboardService dashboardService;

    /**
     * 获取数据面板基本信息
     * @return 数据面板基本信息
     */
    @GetMapping("/info")
    public RestResult<DashboardDTO> info() {
        return RestResult.success(dashboardService.getDashbord());
    }

}
