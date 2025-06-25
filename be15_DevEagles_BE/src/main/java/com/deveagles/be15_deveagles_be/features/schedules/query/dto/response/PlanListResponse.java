package com.deveagles.be15_deveagles_be.features.schedules.query.dto.response;

import java.time.LocalDateTime;

public record PlanListResponse(
    Long id,
    String type, // "plan" 또는 "regular"
    String title,
    String memo,
    String staffName,
    LocalDateTime startAt,
    LocalDateTime endAt) {}
