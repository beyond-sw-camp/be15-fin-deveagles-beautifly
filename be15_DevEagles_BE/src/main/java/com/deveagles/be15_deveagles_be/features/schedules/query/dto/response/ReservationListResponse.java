package com.deveagles.be15_deveagles_be.features.schedules.query.dto.response;

import java.time.LocalDateTime;

public record ReservationListResponse(
    Long reservationId,
    String customerName,
    String customerPhone,
    LocalDateTime reservationStartAt,
    LocalDateTime reservationEndAt,
    String itemNames,
    String staffName,
    String reservationStatusName,
    String staffMemo,
    String reservationMemo) {}
