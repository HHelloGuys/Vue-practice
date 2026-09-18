package com.nova.aichat.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class HttpClientConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // FastAPI 장애 시 요청이 무기한 대기하지 않도록 시간 제한을 설정한다.
        return builder
            .setConnectTimeout(Duration.ofSeconds(3))
            // 로컬 모델의 최초 메모리 로딩 시간을 고려한다.
            .setReadTimeout(Duration.ofSeconds(120))
            .build();
    }
}
