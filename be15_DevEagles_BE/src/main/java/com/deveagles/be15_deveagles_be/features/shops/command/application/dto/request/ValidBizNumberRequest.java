package com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request;

import jakarta.validation.constraints.Pattern;

public record ValidBizNumberRequest(
    @Pattern(regexp = "^\\d{10}$", message = "숫자 10자리여야 합니다.") String bizNumber) {}
