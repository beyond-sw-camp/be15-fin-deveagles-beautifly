package com.deveagles.be15_deveagles_be.features.schedules.query.dto.response;

import java.time.LocalTime;

public record RegularPlanDetailResponse(
    Long regularPlanId,
    Long staffId,
    String staffName,
    Long shopId,
    String title,
    String memo,
    Integer monthlyPlan,
    String weeklyPlan,
    LocalTime startAt,
    LocalTime endAt) {}
