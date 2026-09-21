package com.nova.aichat.conversation;

import java.util.List;

import com.nova.aichat.conversation.dto.ConversationSummary;
import com.nova.aichat.conversation.dto.StoredMessage;
import com.nova.aichat.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 로그인 사용자의 대화 목록과 메시지 조회 API를 제공한다. */
@RestController
@RequestMapping("/api/conversations")
public class ConversationController {
    private final ConversationService conversationService;

    /** 대화 조회 서비스를 주입받는다. */
    public ConversationController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    /** 현재 사용자가 소유한 최근 대화 목록을 반환한다. */
    @GetMapping
    public List<ConversationSummary> conversations(@AuthenticationPrincipal AuthenticatedUser user) {
        return conversationService.findAll(user.getUserId());
    }

    /** 현재 사용자가 소유한 특정 대화의 메시지를 반환한다. */
    @GetMapping("/{conversationId}/messages")
    public List<StoredMessage> messages(@PathVariable Long conversationId,
                                        @AuthenticationPrincipal AuthenticatedUser user) {
        return conversationService.findMessages(conversationId, user.getUserId());
    }
}
