package com.nova.aichat.chat;

import java.util.Collections;
import java.util.List;

import com.nova.aichat.chat.dto.AiChatRequest;
import com.nova.aichat.chat.dto.ChatRequest;
import com.nova.aichat.chat.dto.ChatResponse;
import com.nova.aichat.knowledge.KnowledgeRepository;
import com.nova.aichat.knowledge.RagChunk;
import com.nova.aichat.conversation.ConversationService;
import com.nova.aichat.security.AuthenticatedUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** 대화 저장, RAG 문맥 조회, AI 호출을 조율한다. */
@Service
public class ChatService {

    private final AiChatClient aiChatClient;
    private final KnowledgeRepository knowledgeRepository;
    private final int candidateLimit;
    private final ConversationService conversationService;

    /** 채팅 처리에 필요한 AI 클라이언트와 저장소 및 서비스를 주입받는다. */
    public ChatService(AiChatClient aiChatClient, KnowledgeRepository knowledgeRepository,
                       @Value("${rag.candidate-limit:100}") int candidateLimit,
                       ConversationService conversationService) {
        this.aiChatClient = aiChatClient;
        this.knowledgeRepository = knowledgeRepository;
        this.candidateLimit = candidateLimit;
        this.conversationService = conversationService;
    }

    /** 질문을 검증하고 로그인 사용자의 대화와 메시지를 저장한 뒤 AI 답변을 반환한다. */
    public ChatResponse chat(ChatRequest request, AuthenticatedUser user) {
        // 공백 메시지는 FastAPI로 전달하지 않는다.
        if (request == null || request.getMessage() == null || request.getMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("메시지를 입력해 주세요.");
        }

        List<RagChunk> chunks = user == null
            ? Collections.emptyList()
            : knowledgeRepository.findRecentChunks(user.getUserId(), candidateLimit);
        AiChatRequest aiRequest = new AiChatRequest(request.getMessage().trim(), chunks);

        Long conversationId = null;
        if (user != null) {
            conversationId = conversationService.prepareConversation(
                request.getConversationId(), user.getUserId(), request.getMessage().trim()
            );
            conversationService.saveMessage(conversationId, "user", request.getMessage().trim());
        }
        ChatResponse response = aiChatClient.requestAnswer(aiRequest);

        if (response == null || response.getAnswer() == null) {
            throw new IllegalStateException("AI 서버가 유효한 응답을 반환하지 않았습니다.");
        }

        if (conversationId != null) {
            conversationService.saveMessage(conversationId, "assistant", response.getAnswer());
            response.setConversationId(conversationId);
        }

        return response;
    }
}
