package com.deveagles.be15_deveagles_be.features.schedules.query.dto.response;

import java.time.LocalDateTime;

public record PlanDetailResponse(
    Long planId,
    Long staffId,
    Long shopId,
    String title,
    String memo,
    LocalDateTime startAt,
    LocalDateTime endAt) {}
