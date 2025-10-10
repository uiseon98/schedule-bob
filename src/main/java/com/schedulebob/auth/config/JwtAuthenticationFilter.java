package com.schedulebob.auth.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 모든 HTTP 요청에 대해 JWT 토큰의 유효성을 검증하는 필터.
 * 인증 성공 시 SecurityContext에 인증 정보를 설정하여 이후 요청 처리에 반영된다.
 *
 * 이 필터는 Spring Security 필터 체인 내 UsernamePasswordAuthenticationFilter 앞에 배치된다.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtTokenProvider jwtTokenProvider;

  /**
   * JwtAuthenticationFilter 생성자.
   *
   * @param jwtTokenProvider JWT 토큰을 생성하고 검증하는 유틸리티 객체
   */
  public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  /**
   * HTTP 요청마다 실행되며, Authorization 헤더에서 JWT를 추출하고 검증한다.
   * 유효한 토큰이면 인증 정보를 SecurityContext에 설정한다.
   *
   * @param request  클라이언트로부터의 HTTP 요청
   * @param response 서버에서 클라이언트로의 HTTP 응답
   * @param filterChain 필터 체인 객체, 다음 필터를 실행하기 위해 필요
   * @throws ServletException 서블릿 예외 발생 시
   * @throws IOException 입출력 예외 발생 시
   */
  @Override
  protected void doFilterInternal(HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    // Authorization 헤더로부터 토큰을 추출한다.
    String token = resolveToken(request);

    // 토큰이 존재하고 유효하면 인증 정보를 설정한다.
    if (token != null && jwtTokenProvider.validateToken(token)) {
      String email = jwtTokenProvider.getEmail(token);
      String role = jwtTokenProvider.getRole(token);

      //실제 인증 객체 생성 및 SecurityContext에 등록
      SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(email, null, List.of(authority));

      SecurityContextHolder.getContext().setAuthentication(authentication);

    }

    // 다음 필터로 요청을 넘긴다.
    filterChain.doFilter(request, response);
  }

  /**
   * HTTP 요청의 Authorization 헤더에서 "Bearer " 접두어를 가진 JWT 토큰을 추출한다.
   *
   * @param request HTTP 요청 객체
   * @return JWT 토큰 문자열 또는 존재하지 않으면 null
   */
  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");

    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }

    return null;
  }
}