package com.nova.aichat.security;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/** JWT 기반 무상태 인증과 공개 API 범위를 설정한다. */
@Configuration
public class SecurityConfig {

    /** 회원 비밀번호를 BCrypt로 암호화하고 검증할 인코더를 등록한다. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** 세션을 생성하지 않는 JWT 보안 필터 체인을 구성한다. */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtFilter) throws Exception {
        // JWT 방식은 서버 세션을 만들지 않고 매 요청의 토큰을 검증한다.
        http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers(
                "/api/auth/register",
                "/api/auth/login",
                "/api/health/database",
                "/api/chat"
            ).permitAll()
            .anyRequest().authenticated()
            .and()
            .exceptionHandling()
            .authenticationEntryPoint((request, response, exception) ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            .and()
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
