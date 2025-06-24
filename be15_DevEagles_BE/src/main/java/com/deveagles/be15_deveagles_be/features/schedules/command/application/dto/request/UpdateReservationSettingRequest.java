package com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request;

import java.time.LocalTime;

public record UpdateReservationSettingRequest(
    int availableDay,
    LocalTime availableStartTime,
    LocalTime availableEndTime,
    LocalTime lunchStartTime,
    LocalTime lunchEndTime) {}
