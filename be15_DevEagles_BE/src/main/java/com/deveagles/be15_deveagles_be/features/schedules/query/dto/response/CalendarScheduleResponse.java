package com.deveagles.be15_deveagles_be.features.schedules.query.dto.response;

import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ScheduleType;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CalendarScheduleResponse(
    Long id,
    ScheduleType scheduleType, // PLAN / REGULAR / LEAVE / REGULAR_LEAVE / RESERVATION
    String title, // 제목 or 고객명
    LocalDateTime startAt,
    LocalDateTime endAt,
    String staffName,
    String memo,
    String status,
    String items,
    String staffColor,
    String customerName) {}
