package com.nova.aichat.knowledge;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/** 업로드 문서를 검증하고 청크로 분할해 하나의 트랜잭션으로 저장한다. */
@Service
public class KnowledgeService {
    private static final long MAX_FILE_SIZE = 1024L * 1024L;

    private final KnowledgeRepository knowledgeRepository;
    private final TextChunker textChunker;

    /** 지식 저장소와 텍스트 분할기를 주입받는다. */
    public KnowledgeService(KnowledgeRepository knowledgeRepository, TextChunker textChunker) {
        this.knowledgeRepository = knowledgeRepository;
        this.textChunker = textChunker;
    }

    /** 사용자 문서를 읽고 지식, 문서, 청크를 순서대로 저장한다. */
    @Transactional
    public int upload(Long userId, MultipartFile file) {
        validate(file);
        String fileName = file.getOriginalFilename();
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        try {
            String text = new String(file.getBytes(), StandardCharsets.UTF_8);
            List<String> chunks = textChunker.split(text);
            if (chunks.isEmpty()) {
                throw new IllegalArgumentException("문서에 검색할 텍스트가 없습니다.");
            }
            long knowledgeId = knowledgeRepository.saveKnowledge(userId, removeExtension(fileName));
            long documentId = knowledgeRepository.saveDocument(knowledgeId, fileName, extension);
            knowledgeRepository.saveChunks(documentId, chunks);
            return chunks.size();
        } catch (IOException exception) {
            throw new IllegalArgumentException("문서를 읽을 수 없습니다.", exception);
        }
    }

    /** 파일 존재 여부, 크기, 확장자를 검증한다. */
    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            throw new IllegalArgumentException("업로드할 문서를 선택해 주세요.");
        }
        String name = file.getOriginalFilename().toLowerCase(Locale.ROOT);
        if (!name.endsWith(".txt") && !name.endsWith(".md")) {
            throw new IllegalArgumentException("현재는 .txt와 .md 문서만 지원합니다.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("문서 크기는 1MB 이하여야 합니다.");
        }
    }

    /** 파일명에서 마지막 확장자를 제거한다. */
    private String removeExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex > 0 ? fileName.substring(0, dotIndex) : fileName;
    }
}
