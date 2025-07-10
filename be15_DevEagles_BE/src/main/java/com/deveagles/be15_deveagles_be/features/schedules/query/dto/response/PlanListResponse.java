package com.deveagles.be15_deveagles_be.features.schedules.query.dto.response;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record PlanListResponse(
    Long id,
    Long staffId,
    String staffName,
    String planTitle,
    String planType,
    String repeatRule,
    LocalDateTime startAt,
    LocalDateTime endAt,
    LocalTime startTime,
    LocalTime endTime) {}
