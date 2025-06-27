package com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate;

public enum MessageDeliveryStatus {
  PENDING, // 전송 대기
  SENT, // 전송 완료
  FAIL, // 전송 실패
  CANCELLED // 사용자에 의해 예약 취소
}
