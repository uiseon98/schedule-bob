package com.schedulebob.global.exception;

/**
 * 서비스 계층 인증이나 비즈니스 예외 처리용 커스텀 예외.
 * RuntimeException을 상속하여 사용한다.
 */
public class CustomException extends RuntimeException {
  public CustomException(String message) {
    super(message);
  }
}