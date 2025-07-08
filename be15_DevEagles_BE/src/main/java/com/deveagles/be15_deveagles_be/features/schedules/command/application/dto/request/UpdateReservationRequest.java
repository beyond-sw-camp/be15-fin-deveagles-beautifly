package com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request;

import java.time.LocalDateTime;
import java.util.List;

public record UpdateReservationRequest(
    Long staffId,
    String reservationStatusName,
    String staffMemo,
    String reservationMemo,
    LocalDateTime reservationStartAt,
    LocalDateTime reservationEndAt,
    List<Long> secondaryItemIds) {}
