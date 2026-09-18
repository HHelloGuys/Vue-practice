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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService, UserRepository userRepository) {
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
    }

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
                // 유효하지 않은 토큰은 인증되지 않은 요청으로 처리한다.
            }
        }
        filterChain.doFilter(request, response);
    }
}
