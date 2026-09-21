package com.nova.aichat.knowledge;

/** RAG 검색 후보로 사용할 문서 청크 정보를 담는다. */
public class RagChunk {
    private final Long chunkId;
    private final String fileName;
    private final String content;

    /** 청크 ID, 원본 파일명, 내용을 초기화한다. */
    public RagChunk(Long chunkId, String fileName, String content) {
        this.chunkId = chunkId;
        this.fileName = fileName;
        this.content = content;
    }

    /** 청크 ID를 반환한다. */
    public Long getChunkId() { return chunkId; }
    /** 원본 파일명을 반환한다. */
    public String getFileName() { return fileName; }
    /** 청크 본문을 반환한다. */
    public String getContent() { return content; }
}
