package com.deveagles.be15_deveagles_be.features.schedules.query.dto.response;

import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ScheduleType;
import java.time.LocalDateTime;

public record CalendarRenderedResponse(
    Long id,
    ScheduleType scheduleType,
    String title,
    LocalDateTime startAt,
    LocalDateTime endAt,
    String staffName,
    String memo,
    String staffColor) {}
