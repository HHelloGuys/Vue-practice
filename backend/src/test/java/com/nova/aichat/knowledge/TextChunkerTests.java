package com.nova.aichat.knowledge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

class TextChunkerTests {
    @Test
    void splitsLongTextWithOverlap() {
        TextChunker chunker = new TextChunker(100, 20);
        String text = String.join("", java.util.Collections.nCopies(25, "테스트문장입니다. "));

        List<String> chunks = chunker.split(text);

        assertTrue(chunks.size() > 1);
        assertTrue(chunks.stream().allMatch(chunk -> chunk.length() <= 100));
    }

    @Test
    void keepsShortTextAsOneChunk() {
        assertEquals(1, new TextChunker(100, 20).split("짧은 문서입니다.").size());
    }
}
