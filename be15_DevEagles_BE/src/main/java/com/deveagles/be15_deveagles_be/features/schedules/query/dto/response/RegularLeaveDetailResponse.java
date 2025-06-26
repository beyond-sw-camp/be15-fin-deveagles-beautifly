package com.deveagles.be15_deveagles_be.features.schedules.query.dto.response;

public record RegularLeaveDetailResponse(
    Long regularLeaveId,
    Long staffId,
    String staffName,
    String leaveTitle,
    String repeatRule, // "매주 월요일", "매월 10일" 등으로 포맷팅
    String memo) {}
