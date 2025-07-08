package com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public record CreateReservationFullRequest(
    Long staffId,
    Long customerId, // null이면 미등록 고객
    String staffMemo,
    String reservationMemo,
    LocalDateTime reservationStartAt,
    LocalDateTime reservationEndAt,
    List<Long> secondaryItemIds) {}
