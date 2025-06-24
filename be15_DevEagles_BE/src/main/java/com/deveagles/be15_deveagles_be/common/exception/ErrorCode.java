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

  // 고객 관련 에러 (30000번대)
  CUSTOMER_NOT_FOUND("30001", "고객을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
  CUSTOMER_PHONE_DUPLICATE("30002", "이미 등록된 전화번호입니다", HttpStatus.CONFLICT),
  CUSTOMER_ALREADY_DELETED("30003", "이미 삭제된 고객입니다", HttpStatus.BAD_REQUEST),
  CUSTOMER_ACCESS_DENIED("30004", "해당 고객에 대한 접근 권한이 없습니다", HttpStatus.FORBIDDEN),
  CUSTOMER_INVALID_PHONE_NUMBER("30005", "유효하지 않은 전화번호 형식입니다", HttpStatus.BAD_REQUEST),
  CUSTOMER_INVALID_BIRTHDATE("30006", "유효하지 않은 생년월일입니다", HttpStatus.BAD_REQUEST),

  // 쿠폰 관련 에러 (50000번대)
  COUPON_NOT_FOUND("50001", "쿠폰을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
  COUPON_CODE_DUPLICATE("50002", "이미 존재하는 쿠폰 코드입니다", HttpStatus.CONFLICT),
  COUPON_ALREADY_DELETED("50003", "이미 삭제된 쿠폰입니다", HttpStatus.BAD_REQUEST),
  COUPON_ALREADY_EXPIRED("50004", "만료된 쿠폰입니다", HttpStatus.BAD_REQUEST),
  COUPON_ALREADY_ACTIVE("50005", "이미 활성화된 쿠폰입니다", HttpStatus.BAD_REQUEST),
  COUPON_ALREADY_INACTIVE("50006", "이미 비활성화된 쿠폰입니다", HttpStatus.BAD_REQUEST),
  COUPON_DISCOUNT_CALCULATION_ERROR("50007", "쿠폰 할인 계산 중 오류가 발생했습니다", HttpStatus.BAD_REQUEST),
  COUPON_INVALID_AMOUNT("50008", "유효하지 않은 금액입니다", HttpStatus.BAD_REQUEST),
  COUPON_INVALID_DISCOUNT_RATE("50009", "유효하지 않은 할인율입니다", HttpStatus.BAD_REQUEST),
  DELETED_COUPON_OPERATION_NOT_ALLOWED(
      "50010", "삭제된 쿠폰은 해당 작업을 수행할 수 없습니다", HttpStatus.BAD_REQUEST),

  // 캠페인 관련 에러 (51000번대)
  CAMPAIGN_NOT_FOUND("51001", "캠페인을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
  CAMPAIGN_ALREADY_DELETED("51002", "이미 삭제된 캠페인입니다", HttpStatus.BAD_REQUEST),
  INVALID_CAMPAIGN_DATE_RANGE("51003", "캠페인 종료일은 시작일보다 늦어야 합니다", HttpStatus.BAD_REQUEST),

  // SCHEDULES 관련 에러 (70000번대)
  INVALID_RESERVATION_TIME_RANGE("70001", "예약 시작 시간은 종료 시간보다 빨라야 합니다", HttpStatus.BAD_REQUEST),
  INVALID_LUNCH_TIME_RANGE("70002", "점심시간은 예약 가능 시간 범위 내여야 합니다", HttpStatus.BAD_REQUEST),
  INVALID_LUNCH_TIME_ORDER("70003", "점심시간 시작은 종료보다 빨라야 합니다", HttpStatus.BAD_REQUEST),
  RESERVATION_SETTING_ALREADY_EXISTS("70004", "이미 예약 설정이 존재합니다", HttpStatus.BAD_REQUEST),
  // 일정 삭제 관련 에러 (72000번대)
  INVALID_SCHEDULE_TYPE("72001", "유효하지 않은 일정 타입입니다", HttpStatus.BAD_REQUEST),
  PLAN_NOT_FOUND("72002", "삭제하려는 단기 일정이 존재하지 않습니다", HttpStatus.NOT_FOUND),
  REGULAR_PLAN_NOT_FOUND("72003", "삭제하려는 정기 일정이 존재하지 않습니다", HttpStatus.NOT_FOUND),
  INVALID_SCHEDULE_REPEAT_TYPE("72004", "정기 일정은 요일 또는 월 중 하나만 설정해야 합니다", HttpStatus.BAD_REQUEST);

  private final String code;
  private final String message;
  private final HttpStatus httpStatus;
}
