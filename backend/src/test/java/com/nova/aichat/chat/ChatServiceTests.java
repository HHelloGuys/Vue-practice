package com.nova.aichat.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.nova.aichat.chat.dto.ChatRequest;
import com.nova.aichat.chat.dto.ChatResponse;
import org.junit.jupiter.api.Test;

class ChatServiceTests {

    private final AiChatClient aiChatClient = mock(AiChatClient.class);
    private final ChatService chatService = new ChatService(aiChatClient);

    @Test
    void returnsAiServerAnswer() {
        ChatRequest request = new ChatRequest("안녕하세요");
        when(aiChatClient.requestAnswer(any(ChatRequest.class))).thenReturn(new ChatResponse("반갑습니다"));

        ChatResponse response = chatService.chat(request);

        assertEquals("반갑습니다", response.getAnswer());
    }

    @Test
    void rejectsBlankMessage() {
        // 공백 메시지는 AI 서버를 호출하기 전에 거부되어야 한다.
        assertThrows(
            IllegalArgumentException.class,
            () -> chatService.chat(new ChatRequest("   "))
        );
    }
}
