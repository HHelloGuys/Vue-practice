package com.nova.aichat.common;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

/** REST API에서 발생한 예외를 일관된 JSON 오류 응답으로 변환한다. */
@RestControllerAdvice
public class ApiExceptionHandler {

    /** 잘못된 입력값을 HTTP 400 응답으로 변환한다. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException exception) {
        return ResponseEntity.badRequest()
            .body(Collections.singletonMap("message", exception.getMessage()));
    }

    /** FastAPI 호출 실패를 HTTP 502 응답으로 변환한다. */
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<Map<String, String>> handleAiServerError(RestClientException exception) {
        // 내부 접속 정보는 노출하지 않고 화면에 필요한 오류만 반환한다.
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
            .body(Collections.singletonMap("message", "AI 서버에 연결할 수 없습니다."));
    }

    /** 로그인 정보 불일치를 HTTP 401 응답으로 변환한다. */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentials(BadCredentialsException exception) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Collections.singletonMap("message", exception.getMessage()));
    }

    /** Oracle 연결 또는 SQL 실행 실패를 사용자가 이해할 수 있는 HTTP 503 응답으로 변환한다. */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> handleDatabaseError(DataAccessException exception) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(Collections.singletonMap("message",
                "데이터베이스에 연결하지 못했습니다. Oracle 실행 상태와 접속 정보를 확인해 주세요."));
    }
}
