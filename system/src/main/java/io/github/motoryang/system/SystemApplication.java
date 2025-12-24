package io.github.motoryang.system;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Slf4j
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {
        "io.github.motoryang.security",
        "io.github.motoryang.common",
        "io.github.motoryang.system"
})
public class SystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class);
        log.info("(♥◠‿◠)ノ゙  系统服务启动成功   ლ(´ڡ`ლ)゙");
    }

}