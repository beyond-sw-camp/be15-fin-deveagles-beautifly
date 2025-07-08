package com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request;

import java.time.LocalDateTime;

public record CreatePlanRequest(
    Long staffId,
    String planTitle,
    String planMemo,
    LocalDateTime planStartAt,
    LocalDateTime planEndAt) {}
