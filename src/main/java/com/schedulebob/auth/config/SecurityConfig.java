package com.schedulebob.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스.
 * JWT 인증 필터를 빈으로 등록하고, HTTP 보안 정책을 구성한다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  /**
   * JWT 토큰 생성 및 검증을 담당하는 프로바이더.
   */
  private final JwtTokenProvider jwtTokenProvider;

  /**
   * JWT 인증 필터 빈 등록.
   * SecurityFilterChain에서 이 필터를 사용해 JWT 토큰의 유효성을 검증한다.
   *
   * @return JwtAuthenticationFilter 인스턴스
   */
  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter(jwtTokenProvider);
  }

  /**
   * PasswordEncoder 빈 등록.
   * BCrypt 기반 암호화 방식을 사용하여 비밀번호를 안전하게 처리한다.
   *
   * @return PasswordEncoder 인스턴스
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * HTTP 보안 설정.
   * CSRF 비활성화, 인증이 필요 없는 URL 설정 및 JWT 인증 필터 등록을 수행한다.
   *
   * @param http HttpSecurity 객체
   * @return SecurityFilterChain 구성된 필터 체인
   * @throws Exception 예외 발생 시 던짐
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // JWT 기반 인증을 사용하므로 CSRF는 비활성화
        .csrf().disable()

        // 요청별 권한 설정, 인증 없이 접근 허용할 엔드포인트 지정
        .authorizeHttpRequests(authz -> authz
                .anyRequest().permitAll()
//                .requestMatchers("/api/auth/**").permitAll()
            // .anyRequest().authenticated() // 필요 시 인증 요구
        )

        // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 전에 실행
        .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}