package com.deveagles.be15_deveagles_be.features.auth.command.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CheckEmailRequest(
    @Email @NotBlank(message = "이메일은 필수 입력값입니다.") String email,
    @NotBlank(message = "이름은 필수 입력값입니다.") String staffName) {}
