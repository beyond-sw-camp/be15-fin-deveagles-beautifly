package com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request;

import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ScheduleType;

public record UpdatePlanScheduleRequest(
    ScheduleType fromType,
    Long fromId,
    ScheduleType toType,
    CreatePlanRequest planRequest,
    CreateRegularPlanRequest regularPlanRequest) {}
