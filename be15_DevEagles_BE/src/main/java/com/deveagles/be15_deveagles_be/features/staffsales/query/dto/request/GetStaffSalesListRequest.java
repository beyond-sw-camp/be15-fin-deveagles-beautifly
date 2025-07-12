package com.deveagles.be15_deveagles_be.features.staffsales.query.dto.request;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.SearchMode;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record GetStaffSalesListRequest(
    @NotNull(message = "조회 조건은 필수 입력값입니다.") SearchMode searchMode,
    @NotNull(message = "조회 시작일은 필수입니다.") LocalDate startDate,
    LocalDate endDate) {}
