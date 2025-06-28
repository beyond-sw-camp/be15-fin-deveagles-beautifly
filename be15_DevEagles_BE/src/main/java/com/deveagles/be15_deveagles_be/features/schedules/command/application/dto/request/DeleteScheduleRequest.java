package com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request;

public record DeleteScheduleRequest(Long id, String type // "plan" or "regular"
    ) {}
