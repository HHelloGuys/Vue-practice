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

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public void register(RegisterRequest request) {
        validateRegistration(request);
        String email = request.getEmail().trim().toLowerCase();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        // 원문 비밀번호 대신 BCrypt 해시만 Oracle에 저장한다.
        userRepository.save(email, passwordEncoder.encode(request.getPassword()), request.getName().trim());
    }

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
