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

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) { this.authService = authService; }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Collections.singletonMap("message", "회원가입이 완료되었습니다."));
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) { return authService.login(request); }

    @GetMapping("/me")
    public AuthenticatedUser me(@AuthenticationPrincipal AuthenticatedUser user) { return user; }
}
