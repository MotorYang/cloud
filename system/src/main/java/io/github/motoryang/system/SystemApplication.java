package io.github.motoryang.system;

import io.github.motoryang.security.properties.WhitelistProperties;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;

@Slf4j
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {
        "io.github.motoryang.security",
        "io.github.motoryang.common",
        "io.github.motoryang.system"
})
public class SystemApplication implements CommandLineRunner {

    @Resource
    private Environment env;

    @Resource
    private WhitelistProperties whitelistProperties;

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class);
        log.info("(♥◠‿◠)ノ゙  系统服务启动成功   ლ(´ڡ`ლ)゙");
    }

    @Override
    public void run(String... args) {
        printStartupInfo();
    }

    private void printStartupInfo() {
        String ip = "localhost";
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("无法获取主机IP地址", e);
        }

        String port = env.getProperty("server.port", "8080");
        String contextPath = Optional.ofNullable(env.getProperty("server.servlet.context-path")).orElse("");
        String appName = env.getProperty("spring.application.name", "gateway-service");
        String profile = String.join(",", env.getActiveProfiles().length > 0 ? env.getActiveProfiles() : new String[]{"default"});
        String nacosAddr = env.getProperty("spring.cloud.nacos.server-addr", "未配置");

        log.info("\n" +
                        "=====================================================\n" +
                        "  (♥◠‿◠)ノ゙  系统服务启动成功   ლ(´ڡ`ლ)゙\n" +
                        "=====================================================\n" +
                        "  服务名称: {}\n" +
                        "  运行环境: {}\n" +
                        "  服务端口: {}\n" +
                        "  本地地址: http://localhost:{}{}\n" +
                        "  外部地址: http://{}:{}{}\n" +
                        "  注册中心: {}\n" +
                        "=====================================================",
                appName, profile, port, port, contextPath, ip, port, contextPath, nacosAddr);

        // 白名单信息
        if (whitelistProperties.getWhitelist() != null && !whitelistProperties.getWhitelist().isEmpty()) {
            StringBuilder whitelist = new StringBuilder("\n============== 网关白名单配置 ==============\n");
            for (int i = 0; i < whitelistProperties.getWhitelist().size(); i++) {
                whitelist.append("  ").append(i + 1).append(". ").append(whitelistProperties.getWhitelist().get(i)).append("\n");
            }
            whitelist.append("============================================");
            log.info(whitelist.toString());
        }
    }

}