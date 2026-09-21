package com.nova.aichat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** eGovFramework 기반 Spring Boot 백엔드를 시작하는 진입점이다. */
@SpringBootApplication
public class AiChatBackendApplication {

    /** 애플리케이션 컨텍스트와 내장 웹 서버를 실행한다. */
    public static void main(String[] args) {
        SpringApplication.run(AiChatBackendApplication.class, args);
    }
}
