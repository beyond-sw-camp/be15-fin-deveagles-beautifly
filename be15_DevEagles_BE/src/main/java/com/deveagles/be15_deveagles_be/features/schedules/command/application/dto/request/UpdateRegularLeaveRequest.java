package com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request;

import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.DayOfWeekEnum;

public record UpdateRegularLeaveRequest(
    String regularLeaveTitle,
    Integer monthlyLeave,
    DayOfWeekEnum weeklyLeave,
    String regularLeaveMemo) {}
