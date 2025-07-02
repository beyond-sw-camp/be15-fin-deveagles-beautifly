package com.deveagles.be15_deveagles_be.features.schedules.query.dto.response;

import java.time.LocalTime;

public record CustomerReservationSettingResponse(
    LocalTime availableStartTime,
    LocalTime availableEndTime,
    LocalTime lunchStartTime,
    LocalTime lunchEndTime,
    int reservationTerm) {}
