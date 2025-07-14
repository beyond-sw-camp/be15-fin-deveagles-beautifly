package com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.request;

import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.StaffIncentiveInfo;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.StaffSalesSettingType;
import jakarta.validation.constraints.NotNull;

public record SetIncentiveRequest(
    @NotNull(message = "인센티브 설정 값은 필수 입력값입니다.") Boolean isActive,
    StaffSalesSettingType type,
    StaffIncentiveInfo incentiveInfo) {}
