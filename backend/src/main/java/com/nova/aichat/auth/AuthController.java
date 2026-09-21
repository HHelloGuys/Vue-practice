package com.nova.aichat.auth;

import java.util.Collections;
import java.util.Map;

import com.nova.aichat.auth.dto.AuthResponse;
import com.nova.aichat.auth.dto.LoginRequest;
import com.nova.aichat.auth.dto.RegisterRequest;
import com.nova.aichat.security.AuthenticatedUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 회원가입, 로그인, 현재 사용자 조회용 REST API를 제공한다. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    /** 인증 비즈니스 로직을 주입받는다. */
    public AuthController(AuthService authService) { this.authService = authService; }

    /** 신규 사용자 정보를 검증하고 Oracle에 계정을 생성한다. */
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Collections.singletonMap("message", "회원가입이 완료되었습니다."));
    }

    /** 이메일과 비밀번호를 검증하고 JWT가 포함된 인증 정보를 반환한다. */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) { return authService.login(request); }

    /** 검증된 JWT에 해당하는 현재 로그인 사용자를 반환한다. */
    @GetMapping("/me")
    public AuthenticatedUser me(@AuthenticationPrincipal AuthenticatedUser user) { return user; }
}
