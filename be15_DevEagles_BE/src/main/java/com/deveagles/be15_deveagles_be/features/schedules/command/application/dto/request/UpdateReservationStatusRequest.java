package com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request;

import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ReservationStatusName;
import jakarta.validation.constraints.NotNull;

public record UpdateReservationStatusRequest(
    @NotNull ReservationStatusName reservationStatusName) {}
