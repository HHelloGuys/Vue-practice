package com.nova.aichat.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.nova.aichat.user.UserAccount;
import com.nova.aichat.user.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** 요청의 Bearer JWT를 검증해 Spring Security 인증 객체를 구성한다. */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    /** 토큰 서비스와 사용자 저장소를 주입받는다. */
    public JwtAuthenticationFilter(JwtTokenService jwtTokenService, UserRepository userRepository) {
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
    }

    /** 요청마다 JWT를 확인하고 유효한 사용자만 보안 컨텍스트에 등록한다. */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            try {
                Claims claims = jwtTokenService.parseClaims(authorization.substring(7));
                Long userId = Long.valueOf(claims.getSubject());
                String email = claims.get("email", String.class);
                UserAccount user = userRepository.findByEmail(email).orElse(null);
                if (user != null && user.getUserId().equals(userId) && "ACTIVE".equals(user.getStatus())) {
                    AuthenticatedUser principal = new AuthenticatedUser(userId, email, user.getName());
                    SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList())
                    );
                }
            } catch (JwtException | IllegalArgumentException ignored) {
                // 토큰 없이 온 요청만 게스트로 허용한다. 만료·위조 토큰은 조용히 게스트로 바꾸지 않는다.
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
