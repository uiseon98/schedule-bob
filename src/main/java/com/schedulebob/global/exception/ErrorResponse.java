package com.schedulebob.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 오류 응답 데이터 DTO.
 * API 응답에서 일관된 에러 메시지와 상태 코드를 전달한다.
 */
@Getter
@AllArgsConstructor
public class ErrorResponse {
  private final String message;
  private final int status;
}