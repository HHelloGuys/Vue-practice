package com.nova.aichat.conversation.dto;

/** 최근 대화 목록에 표시할 대화 ID와 제목을 담는다. */
public class ConversationSummary {
    private final Long conversationId;
    private final String title;

    /** 대화 요약 정보를 초기화한다. */
    public ConversationSummary(Long conversationId, String title) {
        this.conversationId = conversationId;
        this.title = title;
    }

    /** 대화 ID를 반환한다. */
    public Long getConversationId() { return conversationId; }
    /** 대화 제목을 반환한다. */
    public String getTitle() { return title; }
}
