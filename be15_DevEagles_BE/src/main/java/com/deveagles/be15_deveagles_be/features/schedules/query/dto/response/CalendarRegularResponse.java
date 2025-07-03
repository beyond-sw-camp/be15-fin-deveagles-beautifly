package com.deveagles.be15_deveagles_be.features.schedules.query.dto.response;

import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ScheduleType;

public record CalendarRegularResponse(
    Long id,
    ScheduleType scheduleType, // REGULAR_PLAN / REGULAR_LEAVE
    String title,
    String staffName,
    String memo,
    String staffColor,

    // 반복 정보
    String weeklyRepeatDay, // 요일 (예: MON, TUE...)
    Integer monthlyRepeatDay, // 월 반복일 (1~31 중 하나)

    // 정기 일정용 시간 정보
    String startTime,
    String endTime) {}
