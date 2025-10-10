package com.schedulebob.auth.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 사용자 계정 및 인증 정보를 관리하는 엔티티.
 * 관리자, 직원, 소셜 로그인 정보를 포함한다.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

  /**
   * 사용자 고유 ID (PK, 자동 생성)
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * 이메일 주소 (로그인 ID로 사용)
   */
  @Column(nullable = false, unique = true, length = 255)
  private String email;

  /**
   * 암호화된 비밀번호
   * 소셜 로그인 사용자는 null일 수 있음.
   */
  @Column(length = 255)
  private String password;

  /**
   * 사용자 이름
   */
  @Column(nullable = false, length = 100)
  private String name;

  /**
   * 연락처 (선택적)
   */
  @Column(length = 20)
  private String phone;

  /**
   * 권한 (admin, employee 등)
   */
  @Column(nullable = false, length = 20)
  private String role;

  /**
   * 소셜 로그인 제공자(ex: google, facebook)
   * 일반 로그인 사용자는 null.
   */
  @Column(length = 50)
  private String socialProvider;

  /**
   * 가입일시 (기본값 현재 시각)
   */
  @Column(nullable = false, updatable = false)
  private LocalDateTime joinedAt = LocalDateTime.now();

  /**
   * 마지막 로그인 시간 (로그인 시마다 갱신)
   */
  private LocalDateTime lastLoginAt;
}