package com.schedulebob.auth.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 응답 데이터 전송 객체(DTO).
 * 인증이 성공할 경우 JWT 토큰 등을 클라이언트에 응답.
 */
@Getter
@Setter
public class LoginResponse {

  private String accessToken;
  private String refreshToken;
  private String tokenType = "Bearer"; // 인증 헤더에 사용하는 접두어

  public LoginResponse(String accessToken, String refreshToken) {
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }
}