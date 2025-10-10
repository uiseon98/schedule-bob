package com.schedulebob.auth.controller;

import com.schedulebob.auth.dto.LoginRequest;
import com.schedulebob.auth.dto.LoginResponse;
import com.schedulebob.auth.service.AuthService;
import com.schedulebob.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 REST API 컨트롤러.
 * JWT 기반 로그인 기능을 제공.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  /**
   * 로그인 엔드포인트
   * @param request LoginRequest (이메일, 비밀번호)
   * @return LoginResponse (Access/Refresh Token)
   * @throws CustomException 인증 실패시 예외 발생
   *
   * - Swagger 적용 시 @ApiOperation, @ApiResponses 등 어노테이션 병행 가능
   */
  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
    LoginResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  /**
   * Refresh Token을 이용한 Access Token 재발급 API
   *
   * @param refreshToken Refresh 토큰 (Authorization 헤더 또는 별도 파라미터로 전달 가능)
   * @return 새로 발급된 Access Token
   */
  @PostMapping("/refresh-token")
  public ResponseEntity<?> refreshAccessToken(@RequestHeader("Authorization") String refreshToken) {
    if (refreshToken == null || !refreshToken.startsWith("Bearer ")) {
      return ResponseEntity.badRequest().body("잘못된 토큰 형식입니다.");
    }

    String token = refreshToken.substring(7);
    String newAccessToken = authService.refreshAccessToken(token);
    return ResponseEntity.ok().body(new AccessTokenResponse(newAccessToken));
  }

  /**
   * Access Token 응답 DTO
   */
  public static class AccessTokenResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public AccessTokenResponse(String accessToken) {
      this.accessToken = accessToken;
    }

    public String getAccessToken() {
      return accessToken;
    }

    public String getTokenType() {
      return tokenType;
    }
  }
}