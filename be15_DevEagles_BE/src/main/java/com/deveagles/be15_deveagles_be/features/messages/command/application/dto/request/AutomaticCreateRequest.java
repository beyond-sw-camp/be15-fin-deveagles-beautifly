package com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request;

import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.AutomaticEventType;
import jakarta.validation.constraints.NotNull;

public record AutomaticCreateRequest(
    @NotNull AutomaticEventType automaticEventType,
    @NotNull String templateContent,
    @NotNull Boolean isActive) {}
