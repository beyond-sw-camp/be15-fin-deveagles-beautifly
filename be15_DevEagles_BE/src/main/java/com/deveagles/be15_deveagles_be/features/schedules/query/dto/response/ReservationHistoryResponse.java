package com.deveagles.be15_deveagles_be.features.schedules.query.dto.response;

import java.time.LocalDateTime;

public record ReservationHistoryResponse(
    Long reservationId,
    String customerName, // 고객 이름
    String itemNames, // 시술 항목 목록 (ex. "염색, 커트")
    String staffName, // 담당자 이름
    LocalDateTime reservationStartAt, // 예약 시작 시각
    String historyType, // 등록 / 수정 / 삭제
    LocalDateTime historyAt // 변경 일시
    ) {}
