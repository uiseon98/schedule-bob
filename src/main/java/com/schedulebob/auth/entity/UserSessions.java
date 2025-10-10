package com.schedulebob.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JWT 기반 세션 관리를 위한 토큰 저장 엔티티.
 * AccessToken과 RefreshToken 발급 및 만료 상태를 관리한다.
 */
@Entity
@Table(name = "user_sessions")
@Getter
@Setter
@NoArgsConstructor
public class UserSessions {

  /**
   * 세션 고유 ID (PK, 자동 생성)
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * 사용자 ID (외래키)
   */
  @Column(nullable = false)
  private Long userId;

  /**
   * 액세스 토큰
   */
  @Lob
  @Column(nullable = false)
  private String accessToken;

  /**
   * 리프레시 토큰
   */
  @Lob
  @Column(nullable = false)
  private String refreshToken;

  /**
   * 토큰 발급 일시
   */
  @Column(nullable = false)
  private LocalDateTime issuedAt;

  /**
   * 토큰 만료 일시
   */
  @Column(nullable = false)
  private LocalDateTime expiredAt;

  // 토큰 갱신, 사용자, 세션 상태 관리용 추가 필드 필요시 확장 가능
}