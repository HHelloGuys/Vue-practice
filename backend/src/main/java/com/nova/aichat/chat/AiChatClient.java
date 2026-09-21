package com.nova.aichat.chat;

import com.nova.aichat.chat.dto.AiChatRequest;
import com.nova.aichat.chat.dto.ChatResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/** eGovFramework에서 FastAPI AI 서버로 채팅 요청을 전달한다. */
@Component
public class AiChatClient {

    private final RestTemplate restTemplate;
    private final String aiServerBaseUrl;

    /** HTTP 클라이언트와 AI 서버 기본 주소를 주입받는다. */
    public AiChatClient(
        RestTemplate restTemplate,
        @Value("${ai.server.base-url}") String aiServerBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.aiServerBaseUrl = aiServerBaseUrl;
    }

    /** 질문과 RAG 후보 문맥을 FastAPI에 전달하고 답변을 반환한다. */
    public ChatResponse requestAnswer(AiChatRequest request) {
        // Vue가 아닌 메인 백엔드에서 AI 전용 FastAPI를 호출한다.
        return restTemplate.postForObject(
            aiServerBaseUrl + "/api/chat",
            request,
            ChatResponse.class
        );
    }
}
