package com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.request;

import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.StaffSalesTargetInfo;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.StaffSalesSettingType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record SetSalesTargetRequest(
    @NotNull LocalDate targetDate,
    @NotNull StaffSalesSettingType type,
    Map<String, Integer> targetAmounts,
    List<StaffSalesTargetInfo> staffTargets) {}
