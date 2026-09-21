package com.nova.aichat.chat;

import com.nova.aichat.chat.dto.ChatRequest;
import com.nova.aichat.chat.dto.ChatResponse;
import com.nova.aichat.security.AuthenticatedUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Vue의 채팅 요청을 받는 REST API를 제공한다. */
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    /** 채팅 비즈니스 로직을 주입받는다. */
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /** 로그인 또는 비로그인 사용자의 질문을 처리한다. */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request,
                                             @AuthenticationPrincipal AuthenticatedUser user) {
        // 화면에는 eGovFramework가 받은 FastAPI 응답만 반환한다.
        return ResponseEntity.ok(chatService.chat(request, user));
    }
}
