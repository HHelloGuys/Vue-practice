package com.nova.aichat.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;

import com.nova.aichat.chat.dto.AiChatRequest;
import com.nova.aichat.chat.dto.ChatRequest;
import com.nova.aichat.chat.dto.ChatResponse;
import com.nova.aichat.knowledge.KnowledgeRepository;
import com.nova.aichat.conversation.ConversationService;
import org.junit.jupiter.api.Test;

class ChatServiceTests {

    private final AiChatClient aiChatClient = mock(AiChatClient.class);
    private final KnowledgeRepository knowledgeRepository = mock(KnowledgeRepository.class);
    private final ConversationService conversationService = mock(ConversationService.class);
    private final ChatService chatService = new ChatService(
        aiChatClient, knowledgeRepository, 100, conversationService
    );

    @Test
    void returnsAiServerAnswer() {
        ChatRequest request = new ChatRequest("안녕하세요");
        when(aiChatClient.requestAnswer(any(AiChatRequest.class))).thenReturn(new ChatResponse("반갑습니다"));

        ChatResponse response = chatService.chat(request, null);

        assertEquals("반갑습니다", response.getAnswer());
    }

    @Test
    void rejectsBlankMessage() {
        // 공백 메시지는 AI 서버를 호출하기 전에 거부되어야 한다.
        assertThrows(
            IllegalArgumentException.class,
            () -> chatService.chat(new ChatRequest("   "), null)
        );
    }
}
