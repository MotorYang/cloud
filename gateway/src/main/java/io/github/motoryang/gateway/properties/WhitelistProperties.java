package io.github.motoryang.gateway.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 网关白名单
 */
@Component
@ConfigurationProperties(prefix = "gateway.security")
public class WhitelistProperties {

    private List<String> whitelist = new ArrayList<>();

    /**
     * 获取规范化的白名单路径（确保以 / 开头）
     */
    public List<String> getWhitelist() {
        List<String> normalizedList = new ArrayList<>();
        for (String path : whitelist) {
            String normalizedPath = path;

            // 确保以 / 开头
            if (!normalizedPath.startsWith("/")) {
                normalizedPath = "/" + normalizedPath;
            }
            normalizedList.add(normalizedPath);
        }
        return normalizedList;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }

}
