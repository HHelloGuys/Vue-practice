package com.nova.aichat.chat;

import com.nova.aichat.chat.dto.ChatRequest;
import com.nova.aichat.chat.dto.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final AiChatClient aiChatClient;

    public ChatService(AiChatClient aiChatClient) {
        this.aiChatClient = aiChatClient;
    }

    public ChatResponse chat(ChatRequest request) {
        // 공백 메시지는 FastAPI로 전달하지 않는다.
        if (request == null || request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("메시지를 입력해 주세요.");
        }

        ChatRequest normalizedRequest = new ChatRequest(request.getMessage().trim());
        ChatResponse response = aiChatClient.requestAnswer(normalizedRequest);

        if (response == null || response.getAnswer() == null) {
            throw new IllegalStateException("AI 서버가 유효한 응답을 반환하지 않았습니다.");
        }

        return response;
    }
}
