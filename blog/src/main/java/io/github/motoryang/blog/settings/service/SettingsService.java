package io.github.motoryang.blog.settings.service;

import io.github.motoryang.blog.article.mapper.ArticleDao;
import io.github.motoryang.blog.settings.converter.ArticleCategoryConverter;
import io.github.motoryang.blog.settings.converter.MusicTrackConverter;
import io.github.motoryang.blog.settings.dto.ArticleCategoryDTO;
import io.github.motoryang.blog.settings.dto.MusicTrackDTO;
import io.github.motoryang.blog.settings.dto.SettingsDTO;
import io.github.motoryang.blog.settings.entity.ArticleCategory;
import io.github.motoryang.blog.settings.entity.MusicTrack;
import io.github.motoryang.blog.settings.mapper.ArticleCategoryMapper;
import io.github.motoryang.blog.settings.mapper.MusicTrackMapper;
import io.github.motoryang.blog.settings.mapper.SettingsMapper;
import io.github.motoryang.blog.settings.request.ApiVerifyResult;
import io.github.motoryang.blog.settings.request.DeleteCheckResult;
import io.github.motoryang.blog.settings.request.SettingsPayload;
import io.github.motoryang.blog.utils.CryptoUtils;
import io.github.motoryang.common.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 系统设置服务实现类
 *
 * @author motoryang
 * @since 2024-01-01
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SettingsService {

    private static final String GEMINI_API_BASE_URL = "https://generativelanguage.googleapis.com/v1/models";
    private static final String GEMINI_API_KEY = "gemini_api_key";
    public static final String REDIS_API_KEY = "blog:api:key";

    private final RestTemplate restTemplate;
    private final SettingsMapper settingsMapper;
    private final MusicTrackMapper musicTrackMapper;
    private final MusicTrackConverter musicTrackConverter;
    private final ArticleCategoryMapper articleCategoryMapper;
    private final ArticleCategoryConverter articleCategoryConverter;
    private final ArticleDao articleDao;
    private final StringRedisTemplate stringRedisTemplate;
    private final CryptoUtils cryptoUtils;

    /**
     * 获取所有设置
     */
    public SettingsDTO getSettings() {
        log.info("获取系统设置");

        SettingsDTO settings = new SettingsDTO();

        // 获取ApiKey
        String apiKey = settingsMapper.getValueByKey(GEMINI_API_KEY);
        String decryptedKey = cryptoUtils.decrypt(apiKey);
        settings.setApiKey(decryptedKey);

        // 获取音乐曲目
        List<MusicTrack> musicTracks = musicTrackMapper.selectAllActive();
        settings.setMusicTracks(musicTracks.stream()
                .map(musicTrackConverter::toDTO)
                .toList());

        // 获取文章分类
        List<ArticleCategory> articleCategories = articleCategoryMapper.selectAllActive();
        settings.setCategories(articleCategories.stream()
                .map(articleCategoryConverter::toDTO)
                .toList());

        return settings;
    }

    /**
     * 保存设置
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveSettings(SettingsPayload payload) {
        log.info("保存系统设置");

        // 1. 保存API密钥
        if (StringUtils.hasText(payload.getApiKey())) {
            String encryptedKey = cryptoUtils.encrypt(payload.getApiKey());
            updateSetting(GEMINI_API_KEY, encryptedKey);
            stringRedisTemplate.opsForValue().set(REDIS_API_KEY, encryptedKey);
        }

        // 2. 保存音乐曲目
        if (payload.getMusicTracks() != null) {
            syncMusicTracks(payload.getMusicTracks());
        }

        // 3. 保存文章分类
        if (payload.getCategories() != null) {
            syncCategories(payload.getCategories());
        }

        log.info("系统设置保存成功");
    }

    /**
     * 验证API密钥
     */
    public ApiVerifyResult verifyApiKey(String apiKey) {
        log.info("验证 API Key");

        // 参数校验
        if (!StringUtils.hasText(apiKey)) {
            throw new ServiceException("API Key 不能为空");
        }

        // API Key 格式校验（Gemini API Key 格式）
        if (!apiKey.matches("^AIza[A-Za-z0-9_-]{35}$")) {
            throw new ServiceException("API Key 格式不正确");
        }

        try {
            String url = GEMINI_API_BASE_URL + "?key=" + apiKey;
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("API Key 验证成功");
                return ApiVerifyResult.builder()
                        .status(true)
                        .message("API Key 验证成功")
                        .build();
            }

            log.warn("API Key 验证失败，状态码: {}", response.getStatusCode());
            throw new ServiceException("API Key 验证失败");

        } catch (RestClientException e) {
            log.error("调用 Gemini API 失败", e);
            throw new ServiceException("API Key 验证失败，请检查网络连接或 API Key 是否正确");
        }
    }

    /**
     * 删除分类前检查
     */
    public DeleteCheckResult deleteCategoryBefore(String category) {
        log.info("检查分类是否可以删除, category: {}", category);

        // 参数校验
        if (!StringUtils.hasText(category)) {
            throw new ServiceException("分类不能为空");
        }

        // 查询该分类下的文章数量
        Integer articleCount = articleDao.countByCategory(category);

        if (articleCount > 0) {
            log.warn("分类下还有 {} 篇文章，无法删除", articleCount);
            return DeleteCheckResult.builder()
                    .status(false)
                    .articleCount(articleCount)
                    .message(String.format("该分类下还有 %d 篇文章，无法删除", articleCount))
                    .build();
        }

        log.info("分类 {} 可以删除", category);
        return DeleteCheckResult.builder()
                .status(true)
                .articleCount(0)
                .message("可以删除")
                .build();
    }

    /**
     * 获取GeminiAi 调用密钥
     */
    public String getApiKey() {
        String encrypted = stringRedisTemplate.opsForValue().get(REDIS_API_KEY);
        if (!StringUtils.hasText(encrypted)) {
            String dbApiKey = settingsMapper.getValueByKey(GEMINI_API_KEY);
            stringRedisTemplate.opsForValue().set(REDIS_API_KEY, dbApiKey);
            return cryptoUtils.decrypt(dbApiKey);
        }
        return cryptoUtils.decrypt(encrypted);
    }

    /**
     * 更新系统设置
     */
    private void updateSetting(String key, String value) {
        settingsMapper.updateByKey(key, value);
    }

    /**
     * 同步音乐曲目
     */
    private void syncMusicTracks(List<MusicTrackDTO> tracks) {
        log.debug("同步音乐曲目, 数量: {}", tracks.size());

        // 1. 收集前端传来的所有已存在的ID
        List<String> frontendIds = tracks.stream()
                .map(MusicTrackDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 2. 删除数据库中存在但前端未传的记录
        if (!frontendIds.isEmpty()) {
            int deletedCount = musicTrackMapper.deleteNotInIds(frontendIds);
            log.debug("删除了 {} 条音乐记录", deletedCount);
        } else {
            int deletedCount = musicTrackMapper.deleteAll();
            log.debug("删除了所有音乐记录，数量: {}", deletedCount);
        }

        // 3. 新增或更新
        for (int i = 0; i < tracks.size(); i++) {
            MusicTrackDTO dto = tracks.get(i);
            dto.setSortOrder(i + 1);

            if (dto.getId() == null) {
                // 新增：生成UUID并插入
                musicTrackMapper.insert(musicTrackConverter.toEntity(dto));
                log.debug("新增音乐: {}", dto.getName());
            } else {
                // 更新：ID不为null说明是已存在的记录
                musicTrackMapper.updateById(musicTrackConverter.toEntity(dto));
                log.debug("更新音乐: {}", dto.getName());
            }
        }
    }

    /**
     * 同步文章分类
     */
    private void syncCategories(List<ArticleCategoryDTO> categories) {
        log.debug("同步文章分类, 数量: {}", categories.size());

        // 1. 收集前端传来的所有已存在的ID
        List<String> frontendIds = categories.stream()
                .map(ArticleCategoryDTO::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 2. 删除数据库中存在但前端未传的记录
        if (!frontendIds.isEmpty()) {
            int deletedCount = articleCategoryMapper.deleteNotInIds(frontendIds);
            log.debug("删除了 {} 个分类", deletedCount);
        } else {
            int deletedCount = articleCategoryMapper.deleteAll();
            log.debug("删除了所有分类，数量: {}", deletedCount);
        }

        // 3. 新增或更新
        for (int i = 0; i < categories.size(); i++) {
            ArticleCategoryDTO dto = categories.get(i);
            dto.setSortOrder(i + 1);

            if (dto.getId() == null) {
                // 新增
                articleCategoryMapper.insert(articleCategoryConverter.toEntity(dto));
                log.debug("新增分类: {}", dto.getNameZh());
            } else {
                // 更新
                articleCategoryMapper.updateById(articleCategoryConverter.toEntity(dto));
                log.debug("更新分类: {}", dto.getNameZh());
            }
        }
    }
}