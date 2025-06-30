package com.deveagles.be15_deveagles_be.features.users.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PatchAccountRequest(
    @NotNull(message = "회원ID는 필수 값입니다.") Long staffId,
    String email,
    @NotBlank(message = "전화번호는 필수 값입니다.") String phoneNumber,
    String password) {}
