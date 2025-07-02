package com.deveagles.be15_deveagles_be.features.users.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PatchProfileRequest(
    @NotBlank(message = "이름은 필수 값입니다.") String staffName,
    @NotBlank(message = "직급은 필수 값입니다.") String grade,
    String colorCode,
    String description) {}
