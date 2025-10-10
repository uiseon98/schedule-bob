package com.schedulebob.auth.repository;

import com.schedulebob.auth.entity.UserSessions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * UserSessions 엔티티 관리를 위한 JPA 리포지토리.
 * Refresh Token 기반 인증 세션 관리에 활용.
 */
public interface UserSessionsRepository extends JpaRepository<UserSessions, Long> {

  /**
   * 유저 ID로 세션 조회 (Refresh Token 검증 및 관리용)
   *
   * @param userId 사용자 고유 ID
   * @return Optional<UserSessions> 세션 존재 시 반환
   */
  Optional<UserSessions> findByUserId(Long userId);

  /**
   * Refresh Token으로 세션 조회 (토큰 재발급 검증용)
   *
   * @param refreshToken 리프레시 토큰 문자열
   * @return Optional<UserSessions> 세션 존재 시 반환
   */
  Optional<UserSessions> findByRefreshToken(String refreshToken);
}