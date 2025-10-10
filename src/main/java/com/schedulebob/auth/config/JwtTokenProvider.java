package com.schedulebob.auth.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;
import javax.crypto.SecretKey;

/**
 * JWT 토큰 생성 및 검증을 담당하는 유틸리티 클래스.
 * Access Token, Refresh Token 모두 처리.
 */
@Component
public class JwtTokenProvider {

  // 시크릿 키 (실제 환경에서는 외부 설정이나 환경변수(yml 등)에서 주입 권장)
  private String secretKey = "REPLACE_WITH_A_STRONG_SECRET_KEY_EXAMPLE_!1234567890";

  // Access Token 만료 시간 (예시: 30분)
  private long accessTokenValidity = 1000L * 60 * 30;

  // Refresh Token 만료 시간 (예시: 14일)
  private long refreshTokenValidity = 1000L * 60 * 60 * 24 * 14;

  private SecretKey key;

  @PostConstruct
  protected void init() {
    // secretKey를 Base64로 인코딩 (jjwt 0.11.x 이상은 SecretKey 필요)
    byte[] keyBytes = Base64.getEncoder().encode(secretKey.getBytes());
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * 사용자 이메일 및 역할을 포함하는 AccessToken 발급
   */
  public String createAccessToken(String email, String role) {
    Claims claims = Jwts.claims().setSubject(email);
    claims.put("role", role);

    Date now = new Date();
    Date expiry = new Date(now.getTime() + accessTokenValidity);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * 사용자 식별자만 포함하는 RefreshToken 발급
   */
  public String createRefreshToken(String email) {
    Date now = new Date();
    Date expiry = new Date(now.getTime() + refreshTokenValidity);

    return Jwts.builder()
        .setSubject(email)
        .setIssuedAt(now)
        .setExpiration(expiry)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * JWT 토큰에서 이메일(Subject) 추출
   */
  public String getEmail(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  /**
   * JWT 토큰에서 역할(Claims) 추출
   */
  public String getRole(String token) {
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();
    return claims.get("role", String.class);
  }

  /**
   * 토큰 유효성 검증
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  /**
   * 토큰 만료시간 조회
   */
  public Date getExpiration(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getExpiration();
  }
}