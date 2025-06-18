package com.deveagles.be15_deveagles_be.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode implements ErrorCodeType {

  // 공통 에러
  VALIDATION_ERROR("00001", "유효성 검증에 실패했습니다", HttpStatus.BAD_REQUEST),
  INVALID_INPUT_VALUE("00002", "잘못된 입력값입니다", HttpStatus.BAD_REQUEST),
  METHOD_NOT_ALLOWED("00003", "허용되지 않는 HTTP 메소드입니다", HttpStatus.METHOD_NOT_ALLOWED),
  AUTHENTICATION_FAILED("00004", "인증에 실패했습니다", HttpStatus.UNAUTHORIZED),
  ACCESS_DENIED("00005", "접근 권한이 없습니다", HttpStatus.FORBIDDEN),
  RESOURCE_NOT_FOUND("00006", "요청한 리소스를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
  DUPLICATE_RESOURCE("00007", "중복된 리소스입니다", HttpStatus.CONFLICT),
  INTERNAL_SERVER_ERROR("00999", "서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),

  // 쿠폰 관련 에러 (50000번대)
  COUPON_NOT_FOUND("50001", "쿠폰을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
  COUPON_CODE_DUPLICATE("50002", "이미 존재하는 쿠폰 코드입니다", HttpStatus.CONFLICT),
  COUPON_ALREADY_DELETED("50003", "이미 삭제된 쿠폰입니다", HttpStatus.BAD_REQUEST),
  COUPON_ALREADY_EXPIRED("50004", "만료된 쿠폰입니다", HttpStatus.BAD_REQUEST),
  COUPON_ALREADY_ACTIVE("50005", "이미 활성화된 쿠폰입니다", HttpStatus.BAD_REQUEST),
  COUPON_ALREADY_INACTIVE("50006", "이미 비활성화된 쿠폰입니다", HttpStatus.BAD_REQUEST),
  DELETED_COUPON_OPERATION_NOT_ALLOWED(
      "50007", "삭제된 쿠폰은 해당 작업을 수행할 수 없습니다", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final HttpStatus httpStatus;
}
