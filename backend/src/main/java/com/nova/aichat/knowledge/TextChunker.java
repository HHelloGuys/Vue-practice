package com.nova.aichat.knowledge;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** 긴 문서를 RAG 검색에 사용할 겹치는 텍스트 청크로 분할한다. */
@Component
public class TextChunker {
    private final int chunkSize;
    private final int chunkOverlap;

    /** 청크 크기와 청크 사이의 중복 글자 수를 설정한다. */
    public TextChunker(@Value("${rag.chunk-size:800}") int chunkSize,
                       @Value("${rag.chunk-overlap:100}") int chunkOverlap) {
        if (chunkSize < 100 || chunkOverlap < 0 || chunkOverlap >= chunkSize) {
            throw new IllegalArgumentException("RAG 청크 설정이 올바르지 않습니다.");
        }
        this.chunkSize = chunkSize;
        this.chunkOverlap = chunkOverlap;
    }

    /** 정규화한 텍스트를 설정된 크기와 중복 범위로 분할한다. */
    public List<String> split(String text) {
        String normalized = text.replace("\r\n", "\n").replace('\r', '\n').trim();
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < normalized.length()) {
            int end = Math.min(start + chunkSize, normalized.length());
            if (end < normalized.length()) {
                int paragraphEnd = normalized.lastIndexOf("\n\n", end);
                int sentenceEnd = normalized.lastIndexOf('.', end);
                int boundary = Math.max(paragraphEnd, sentenceEnd);
                if (boundary > start + (chunkSize / 2)) {
                    end = boundary + 1;
                }
            }
            String chunk = normalized.substring(start, end).trim();
            if (!chunk.isEmpty()) {
                chunks.add(chunk);
            }
            if (end >= normalized.length()) {
                break;
            }
            start = Math.max(start + 1, end - chunkOverlap);
        }
        return chunks;
    }
}
