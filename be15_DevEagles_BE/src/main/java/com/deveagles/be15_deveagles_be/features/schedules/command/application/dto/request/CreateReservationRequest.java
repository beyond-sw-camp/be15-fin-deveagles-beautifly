package com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public record CreateReservationRequest(
    Long shopId,
    Long staffId,
    Long customerId,
    String customerName,
    String customerPhone,
    String reservationMemo,
    LocalDateTime reservationStartAt,
    LocalDateTime reservationEndAt,
    List<Long> secondaryItemIds) {}
