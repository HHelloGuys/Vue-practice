package com.nova.aichat.conversation;

import java.util.List;

import com.nova.aichat.conversation.dto.ConversationSummary;
import com.nova.aichat.conversation.dto.StoredMessage;
import org.springframework.stereotype.Service;

/** 대화 생성, 소유권 검증, 메시지 조회와 저장 규칙을 처리한다. */
@Service
public class ConversationService {
    private final ConversationRepository conversationRepository;

    /** 대화 저장소를 주입받는다. */
    public ConversationService(ConversationRepository conversationRepository) {
        this.conversationRepository = conversationRepository;
    }

    /** 새 대화를 만들거나 기존 대화의 사용자 소유권을 검증한다. */
    public long prepareConversation(Long conversationId, Long userId, String firstMessage) {
        if (conversationId == null) {
            String title = firstMessage.length() > 20
                ? firstMessage.substring(0, 20) + "…"
                : firstMessage;
            return conversationRepository.create(userId, title);
        }
        if (!conversationRepository.belongsTo(conversationId, userId)) {
            throw new IllegalArgumentException("접근할 수 없는 대화입니다.");
        }
        return conversationId;
    }

    /** 지정된 대화에 메시지를 저장한다. */
    public void saveMessage(Long conversationId, String role, String content) {
        conversationRepository.saveMessage(conversationId, role, content);
    }

    /** 사용자의 대화 목록을 반환한다. */
    public List<ConversationSummary> findAll(Long userId) {
        return conversationRepository.findAll(userId);
    }

    /** 사용자 소유 대화의 메시지 목록을 반환한다. */
    public List<StoredMessage> findMessages(Long conversationId, Long userId) {
        if (!conversationRepository.belongsTo(conversationId, userId)) {
            throw new IllegalArgumentException("접근할 수 없는 대화입니다.");
        }
        return conversationRepository.findMessages(conversationId, userId);
    }
}
