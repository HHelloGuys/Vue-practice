package com.nova.aichat.knowledge;

import java.util.LinkedHashMap;
import java.util.Map;

import com.nova.aichat.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** 로그인 사용자의 RAG 지식 문서 등록 API를 제공한다. */
@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {
    private final KnowledgeService knowledgeService;

    /** 지식 문서 처리 서비스를 주입받는다. */
    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    /** 텍스트 문서를 검증하고 분할해 사용자 지식으로 등록한다. */
    @PostMapping("/documents")
    public ResponseEntity<Map<String, Object>> upload(
        @AuthenticationPrincipal AuthenticatedUser user,
        @RequestPart("file") MultipartFile file
    ) {
        int chunkCount = knowledgeService.upload(user.getUserId(), file);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "지식 문서가 등록되었습니다.");
        response.put("chunkCount", chunkCount);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
