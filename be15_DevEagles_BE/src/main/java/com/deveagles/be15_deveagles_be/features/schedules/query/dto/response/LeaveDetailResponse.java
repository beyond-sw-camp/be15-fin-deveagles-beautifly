package com.deveagles.be15_deveagles_be.features.schedules.query.dto.response;

import java.time.LocalDate;

public record LeaveDetailResponse(
    Long leaveId,
    Long staffId,
    String staffName,
    String leaveTitle,
    LocalDate leaveDate,
    String memo) {}
