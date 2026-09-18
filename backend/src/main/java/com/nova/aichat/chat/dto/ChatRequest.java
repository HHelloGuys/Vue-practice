package com.nova.aichat.chat.dto;

public class ChatRequest {

    private String message;

    public ChatRequest() {
        // Jackson이 JSON 요청 객체를 생성할 때 사용하는 기본 생성자다.
    }

    public ChatRequest(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
