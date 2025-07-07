package com.deveagles.be15_deveagles_be.features.schedules.query.dto.request;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record ReservationSearchRequest(
    Long shopId,
    Long staffId,
    String reservationStatusName,
    String customerKeyword,
    LocalDate from,
    LocalDate to) {}
