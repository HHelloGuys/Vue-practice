package com.nova.aichat.chat.dto;

public class ChatResponse {

    private String answer;

    public ChatResponse() {
        // Jackson이 FastAPI 응답을 변환할 때 사용하는 기본 생성자다.
    }

    public ChatResponse(String answer) {
        this.answer = answer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
