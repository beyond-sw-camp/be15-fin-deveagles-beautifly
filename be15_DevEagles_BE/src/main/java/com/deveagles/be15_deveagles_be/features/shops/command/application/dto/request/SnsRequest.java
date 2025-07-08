package com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record SnsRequest(
    Long snsId,
    @NotBlank(message = "SNS 타입은 필수 입력값입니다.") String type,
    @NotBlank(message = "SNS 주소는 필수 입력값입니다.") String snsAddress) {}
