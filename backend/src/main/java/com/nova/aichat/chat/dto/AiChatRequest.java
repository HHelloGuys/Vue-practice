package com.nova.aichat.chat.dto;

import java.util.Collections;
import java.util.List;

import com.nova.aichat.knowledge.RagChunk;

/** FastAPI에 전달할 사용자 질문과 RAG 후보 청크를 담는다. */
public class AiChatRequest {
    private final String message;
    private final List<RagChunk> chunks;

    /** AI 요청 메시지와 후보 청크를 초기화한다. */
    public AiChatRequest(String message, List<RagChunk> chunks) {
        this.message = message;
        this.chunks = chunks == null ? Collections.emptyList() : chunks;
    }

    /** 사용자 질문을 반환한다. */
    public String getMessage() { return message; }
    /** RAG 검색 후보 청크를 반환한다. */
    public List<RagChunk> getChunks() { return chunks; }
}
