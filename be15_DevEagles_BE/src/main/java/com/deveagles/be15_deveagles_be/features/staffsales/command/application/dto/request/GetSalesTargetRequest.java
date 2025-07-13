package com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record GetSalesTargetRequest(
    @NotNull(message = "목표 매출 대상 날짜는 필수 입력값 입니다.") LocalDate targetDate) {}
