package com.nova.aichat.chat;

import com.nova.aichat.chat.dto.ChatRequest;
import com.nova.aichat.chat.dto.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AiChatClient {

    private final RestTemplate restTemplate;
    private final String aiServerBaseUrl;

    public AiChatClient(
        RestTemplate restTemplate,
        @Value("${ai.server.base-url}") String aiServerBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.aiServerBaseUrl = aiServerBaseUrl;
    }

    public ChatResponse requestAnswer(ChatRequest request) {
        // Vue가 아닌 메인 백엔드에서 AI 전용 FastAPI를 호출한다.
        return restTemplate.postForObject(
            aiServerBaseUrl + "/api/chat",
            request,
            ChatResponse.class
        );
    }
}
