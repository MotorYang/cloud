package io.github.motoryang.blog.settings.controller;

import io.github.motoryang.blog.settings.dto.SettingsDTO;
import io.github.motoryang.blog.settings.request.ApiKeyRequest;
import io.github.motoryang.blog.settings.request.ApiVerifyResult;
import io.github.motoryang.blog.settings.request.DeleteCheckResult;
import io.github.motoryang.blog.settings.request.SettingsPayload;
import io.github.motoryang.blog.settings.service.SettingsService;
import io.github.motoryang.common.domain.RestResult;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@Validated
@RestController
@RequestMapping("/settings")
@RequiredArgsConstructor  // 使用构造器注入替代 @Resource
public class SettingsController {

    private final SettingsService settingsService;

    /**
     * 获取系统设置信息
     *
     * @return 设置信息
     */
    @GetMapping("/info")
    public RestResult<SettingsDTO> info() {
        SettingsDTO settings = settingsService.getSettings();
        return RestResult.success(settings);
    }

    /**
     * 保存/更新系统设置
     *
     * @param payload 设置数据
     * @return 操作结果
     */
    @PutMapping("/publish")
    public RestResult<Void> publish(@Valid @RequestBody SettingsPayload payload) {
        settingsService.saveSettings(payload);
        return RestResult.success();
    }

    /**
     * 验证 API Key 是否有效
     *
     * @param request 包含 apiKey 的请求体
     * @return 验证结果
     */
    @PostMapping("/api-verify")
    public RestResult<ApiVerifyResult> apiVerify(@Valid @RequestBody ApiKeyRequest request) {
        ApiVerifyResult result = settingsService.verifyApiKey(request.getApiKey());
        return RestResult.success(result);
    }

    /**
     * 删除分类前的检查
     *
     * @param categoryId 分类ID
     * @return 检查结果
     */
    @GetMapping("/delete-check/{categoryId}")
    public RestResult<DeleteCheckResult> deleteCheck(
            @PathVariable("categoryId") @NotBlank(message = "分类ID不能为空") String categoryId) {
        DeleteCheckResult result = settingsService.deleteCategoryBefore(categoryId);
        return RestResult.success(result);
    }
}
