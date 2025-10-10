package com.schedulebob.auth.service;

import com.schedulebob.auth.dto.LoginRequest;
import com.schedulebob.auth.dto.LoginResponse;
import com.schedulebob.auth.entity.User;
import com.schedulebob.auth.entity.UserSessions;
import com.schedulebob.global.exception.CustomException;
import com.schedulebob.auth.repository.UserRepository;
import com.schedulebob.auth.repository.UserSessionsRepository;
import com.schedulebob.auth.config.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 인증 비즈니스 로직 서비스.
 * 로그인, 토큰 발급 및 리프레시 토큰 저장/검증 담당.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final UserSessionsRepository userSessionsRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  /**
   * 로그인 처리 및 AccessToken/RefreshToken 발급.
   * RefreshToken DB에 저장.
   *
   * @param request 로그인 요청 DTO
   * @return 로그인 응답 DTO (토큰 포함)
   */
  @Transactional
  public LoginResponse login(LoginRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

    if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new CustomException("비밀번호가 일치하지 않습니다.");
    }

    String accessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

    user.setLastLoginAt(LocalDateTime.now());
    userRepository.save(user);

    // 기존 세션이 있으면 갱신, 없으면 새로 저장
    Optional<UserSessions> optionalSession = userSessionsRepository.findByUserId(user.getId());

    UserSessions session = optionalSession.orElse(new UserSessions());
    session.setUserId(user.getId());
    session.setAccessToken(accessToken);
    session.setRefreshToken(refreshToken);
    session.setIssuedAt(LocalDateTime.now());
    session.setExpiredAt(LocalDateTime.now().plusDays(14));  // RefreshToken 만료일 예시
    userSessionsRepository.save(session);

    return new LoginResponse(accessToken, refreshToken);
  }

  /**
   * RefreshToken으로 AccessToken 재발급
   *
   * @param refreshToken 클라이언트로부터 받은 Refresh Token
   * @return 새로운 AccessToken
   */
  @Transactional
  public String refreshAccessToken(String refreshToken) {
    if (!jwtTokenProvider.validateToken(refreshToken)) {
      throw new CustomException("리프레시 토큰이 유효하지 않습니다.");
    }

    String email = jwtTokenProvider.getEmail(refreshToken);

    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new CustomException("사용자를 찾을 수 없습니다."));

    UserSessions session = userSessionsRepository.findByUserId(user.getId())
        .orElseThrow(() -> new CustomException("세션이 존재하지 않습니다."));

    if (!refreshToken.equals(session.getRefreshToken())) {
      throw new CustomException("리프레시 토큰이 일치하지 않습니다.");
    }

    String newAccessToken = jwtTokenProvider.createAccessToken(user.getEmail(), user.getRole());

    // Access Token 갱신은 세션에 저장하는 정책에 따라 변경 가능
    session.setAccessToken(newAccessToken);
    userSessionsRepository.save(session);

    return newAccessToken;
  }
}