package com.deveagles.be15_deveagles_be.features.users.command.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record GetAccountRequest(@NotNull(message = "계정 정보가 존재하지 않습니다.") Long staffId) {}
