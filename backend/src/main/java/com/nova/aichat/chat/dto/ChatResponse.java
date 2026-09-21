package com.nova.aichat.chat.dto;

import java.util.Collections;
import java.util.List;

/** AI 답변, 참고 문서, 대화 ID를 Vue에 반환한다. */
public class ChatResponse {

    private String answer;
    private List<String> sources = Collections.emptyList();
    private Long conversationId;

    /** JSON 역직렬화를 위한 기본 생성자이다. */
    public ChatResponse() {
        // Jackson이 FastAPI 응답을 변환할 때 사용하는 기본 생성자다.
    }

    /** 답변만 지정하는 간편 생성자이다. */
    public ChatResponse(String answer) {
        this.answer = answer;
    }

    /** AI 답변을 반환한다. */
    public String getAnswer() {
        return answer;
    }

    /** AI 답변을 설정한다. */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /** 답변에 활용된 출처 목록을 반환한다. */
    public List<String> getSources() { return sources; }
    /** 답변에 활용된 출처 목록을 설정한다. */
    public void setSources(List<String> sources) {
        this.sources = sources == null ? Collections.emptyList() : sources;
    }
    /** 저장된 대화 ID를 반환한다. */
    public Long getConversationId() { return conversationId; }
    /** 저장된 대화 ID를 설정한다. */
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
}
