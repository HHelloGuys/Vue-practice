package com.nova.aichat.chat.dto;

/** Vue에서 전달한 채팅 메시지와 선택된 대화 ID를 받는다. */
public class ChatRequest {

    private String message;
    private Long conversationId;

    /** JSON 역직렬화를 위한 기본 생성자이다. */
    public ChatRequest() {
        // Jackson이 JSON 요청 객체를 생성할 때 사용하는 기본 생성자다.
    }

    /** 메시지만 지정하는 테스트 및 간편 생성자이다. */
    public ChatRequest(String message) {
        this.message = message;
    }

    /** 사용자 메시지를 반환한다. */
    public String getMessage() {
        return message;
    }

    /** 사용자 메시지를 설정한다. */
    public void setMessage(String message) {
        this.message = message;
    }

    /** 기존 대화 ID를 반환한다. */
    public Long getConversationId() { return conversationId; }
    /** 기존 대화 ID를 설정한다. */
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
}
