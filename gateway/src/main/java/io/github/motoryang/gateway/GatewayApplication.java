package io.github.motoryang.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@Slf4j
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "io.github.motoryang")
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class);
        log.info("(♥◠‿◠)ノ゙  网关服务启动成功   ლ(´ڡ`ლ)゙");
    }

}
