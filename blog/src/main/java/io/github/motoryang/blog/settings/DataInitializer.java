package io.github.motoryang.blog.settings;

import io.github.motoryang.blog.settings.entity.ArticleCategory;
import io.github.motoryang.blog.settings.entity.MusicTrack;
import io.github.motoryang.blog.settings.entity.Settings;
import io.github.motoryang.blog.settings.mapper.ArticleCategoryMapper;
import io.github.motoryang.blog.settings.mapper.MusicTrackMapper;
import io.github.motoryang.blog.settings.mapper.SettingsMapper;
import io.github.motoryang.blog.settings.service.SettingsService;
import io.github.motoryang.blog.utils.CryptoUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 博客设置的数据初始化
 */
@Slf4j
@Component("settingsDataInitializer")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SettingsMapper settingsMapper;
    private final MusicTrackMapper musicTrackMapper;
    private final ArticleCategoryMapper articleCategoryMapper;
    private final CryptoUtils cryptoUtils;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void run(String... args) throws Exception {
      Long settingCount = settingsMapper.selectCount(null);
      Long musicTrackCount = musicTrackMapper.selectCount(null);
      Long articleCategoryCount = articleCategoryMapper.selectCount(null);


      if (settingCount == 0) {
          log.info("===初始化ApiKey===");
          String apiKey = "AIzaSyDQ6Z4eZxwXVvv5T-fq5gIKtwL4jzAgFXc";
          String enApiKey = cryptoUtils.encrypt(apiKey);
          Settings settings = Settings.builder()
                  .key("gemini_api_key")
                  .value(enApiKey)
                  .description("调用GeminiAi的密钥")
                  .build();
          log.info(settings.toString());
          stringRedisTemplate.opsForValue().set(SettingsService.REDIS_API_KEY, enApiKey);
          settingsMapper.insert(settings);
      } else {
          log.info("缓存apiKey到Redis中...");
          String cipherText = settingsMapper.getValueByKey("gemini_api_key");
          stringRedisTemplate.opsForValue().set(SettingsService.REDIS_API_KEY, cipherText);
      }

      if (musicTrackCount == 0) {
          log.info("===初始化音乐播放器设置===");
          List<MusicTrack> musicTrackList = Arrays.asList(
                  MusicTrack.builder()
                          .name("红色高跟鞋")
                          .url("http://music.163.com/song/media/outer/url?id=2046829393.mp3")
                          .author("蔡健雅")
                          .sortOrder(0)
                          .build(),
                  MusicTrack.builder()
                          .name("起风了")
                          .url("http://music.163.com/song/media/outer/url?id=1330348068.mp3")
                          .author("买辣椒也用券")
                          .sortOrder(1)
                          .build(),
                  MusicTrack.builder()
                          .name("城南花已开")
                          .url("http://music.163.com/song/media/outer/url?id=468176711.mp3")
                          .author("三亩地")
                          .sortOrder(2)
                          .build()
          );
          log.info(musicTrackList.toString());
          musicTrackMapper.insert(musicTrackList);
      }

      if (articleCategoryCount == 0) {
          log.info("===初始化文章分类设置===");
          List<ArticleCategory> articleCategories = Arrays.asList(
                  ArticleCategory.builder()
                          .code("tech")
                          .nameZh("技术")
                          .nameEn("Tech")
                          .sortOrder(0)
                          .build(),
                  ArticleCategory.builder()
                          .code("life")
                          .nameZh("生活")
                          .nameEn("Life")
                          .sortOrder(1)
                          .build(),
                  ArticleCategory.builder()
                          .code("trip")
                          .nameZh("旅行")
                          .nameEn("Trip")
                          .sortOrder(2)
                          .build(),
                  ArticleCategory.builder()
                          .code("food")
                          .nameZh("美食")
                          .nameEn("Food")
                          .sortOrder(3)
                          .build(),
                  ArticleCategory.builder()
                          .code("random")
                          .nameZh("随笔")
                          .nameEn("Random")
                          .sortOrder(4)
                          .build()
          );
          log.info(articleCategories.toString());
          articleCategoryMapper.insert(articleCategories);
      }
    }
}
