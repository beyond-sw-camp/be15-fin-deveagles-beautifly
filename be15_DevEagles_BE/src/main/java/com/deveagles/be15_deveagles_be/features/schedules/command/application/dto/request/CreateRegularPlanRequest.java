package com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request;

import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.DayOfWeekEnum;
import java.time.LocalTime;

public record CreateRegularPlanRequest(
    Long staffId,
    Long shopId,
    String regularPlanTitle,
    Integer monthlyPlan,
    DayOfWeekEnum weeklyPlan,
    String regularPlanMemo,
    LocalTime regularPlanStartAt,
    LocalTime regularPlanEndAt) {}
