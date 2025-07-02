package com.deveagles.be15_deveagles_be.features.schedules.query.dto.request;

import java.time.LocalDateTime;

public record CalendarScheduleRequest(
    LocalDateTime from,
    LocalDateTime to,
    String customerKeyword, // 고객명 또는 연락처
    String itemKeyword, // 시술명
    Long staffId, // 담당자 ID
    String scheduleType // PLAN, LEAVE, RESERVATION 중 선택
    ) {}
