package com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate;

public enum MessageSendingType {
  IMMEDIATE, // 즉시 발송
  RESERVATION, // 예약 발송
  AUTOMATIC // 자동 발송 (신규 고객 등록, 예약 등록 등 이벤트 기반)
}
