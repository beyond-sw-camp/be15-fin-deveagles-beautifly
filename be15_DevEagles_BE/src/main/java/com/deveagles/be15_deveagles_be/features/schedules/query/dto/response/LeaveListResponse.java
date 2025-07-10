package com.deveagles.be15_deveagles_be.features.schedules.query.dto.response;

import java.time.LocalDate;

public record LeaveListResponse(
    Long id, // 단기: leaveId, 정기: regularLeaveId
    Long staffId,
    String staffName,
    String leaveTitle,
    String leaveType, // "regular" or "leave"
    String repeatRule, // 정기: "매주 월요일 반복" 또는 "매월 5일 반복"
    LocalDate leaveDate // 단기인 경우 날짜, 정기는 null
    ) {}
