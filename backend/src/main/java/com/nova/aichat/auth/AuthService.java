package com.nova.aichat.auth;

import com.nova.aichat.auth.dto.AuthResponse;
import com.nova.aichat.auth.dto.LoginRequest;
import com.nova.aichat.auth.dto.RegisterRequest;
import com.nova.aichat.security.AuthenticatedUser;
import com.nova.aichat.security.JwtTokenService;
import com.nova.aichat.user.UserAccount;
import com.nova.aichat.user.UserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/** 회원가입과 로그인 절차를 수행하는 인증 서비스이다. */
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    /** 인증에 필요한 저장소, 암호화기, 토큰 서비스를 주입받는다. */
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    /** 회원가입 입력값을 검증하고 BCrypt 비밀번호로 사용자를 저장한다. */
    public void register(RegisterRequest request) {
        validateRegistration(request);
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        // 원문 비밀번호 대신 BCrypt 해시만 Oracle에 저장한다.
        userRepository.save(email, passwordEncoder.encode(request.getPassword()), request.getName().trim());
    }

    /** 계정 상태와 비밀번호를 확인한 뒤 JWT 인증 응답을 생성한다. */
    public AuthResponse login(LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        UserAccount user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
            .filter(value -> "ACTIVE".equals(value.getStatus()))
            .orElseThrow(() -> new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        AuthenticatedUser principal = new AuthenticatedUser(user.getUserId(), user.getEmail(), user.getName());
        return new AuthResponse(jwtTokenService.createToken(principal),
            jwtTokenService.getExpirationSeconds(), principal);
    }

    /** 회원가입 이메일, 비밀번호 및 이름의 기본 형식을 검증한다. */
    private void validateRegistration(RegisterRequest request) {
        if (request == null || request.getEmail() == null || !request.getEmail().contains("@")) {
            throw new IllegalArgumentException("올바른 이메일을 입력해 주세요.");
        }
        if (request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("비밀번호는 8자 이상이어야 합니다.");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("이름을 입력해 주세요.");
        }
    }
}
