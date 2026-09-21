package com.nova.aichat.conversation.dto;

/** Oracle에서 조회한 개별 대화 메시지를 표현한다. */
public class StoredMessage {
    private final Long messageId;
    private final String role;
    private final String content;
    private final String createdAt;

    /** 저장된 메시지의 모든 필드를 초기화한다. */
    public StoredMessage(Long messageId, String role, String content, String createdAt) {
        this.messageId = messageId;
        this.role = role;
        this.content = content;
        this.createdAt = createdAt;
    }

    /** 메시지 ID를 반환한다. */
    public Long getMessageId() { return messageId; }
    /** 메시지 작성 주체 역할을 반환한다. */
    public String getRole() { return role; }
    /** 메시지 본문을 반환한다. */
    public String getContent() { return content; }
    /** 메시지 생성 시각 문자열을 반환한다. */
    public String getCreatedAt() { return createdAt; }
}
