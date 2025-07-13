package com.deveagles.be15_deveagles_be.features.staffsales.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.request.SetIncentiveRequest;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.request.SetSalesTargetRequest;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response.IncentiveListResult;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.service.IncentiveCommandService;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.service.SalesTargetCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/staff-sales")
@Tag(name = "직원 결산 관리", description = "직원 결산 관리 관련 API")
public class StaffSalesCommandController {

  private final IncentiveCommandService incentiveCommandService;
  private final SalesTargetCommandService salesTargetCommandService;

  @GetMapping("/incentive")
  @Operation(summary = "직원 인센티브 조회", description = "매장 별 직원에게 적용하는 인센티브 내역을 조회합니다.")
  public ResponseEntity<ApiResponse<IncentiveListResult>> getIncentives(
      @AuthenticationPrincipal CustomUser customUser) {

    IncentiveListResult response = incentiveCommandService.getIncentives(customUser.getShopId());

    return ResponseEntity.ok().body(ApiResponse.success(response));
  }

  @PostMapping("/incentive")
  @Operation(summary = "직원 인센티브 설정", description = "매장 별 직원에게 적용하는 인센티브 내역을 설정합니다.")
  public ResponseEntity<ApiResponse<Void>> setIncentives(
      @AuthenticationPrincipal CustomUser customUser,
      @RequestBody @Valid SetIncentiveRequest request) {

    incentiveCommandService.setIncentive(customUser.getShopId(), request);

    return ResponseEntity.ok().body(ApiResponse.success(null));
  }

  @PostMapping("/sales-target")
  @Operation(summary = "직원 목표 매출 설정", description = "매장 별 직원에게 적용하는 목표 매출 내역을 설정합니다.")
  public ResponseEntity<ApiResponse<Void>> setSalesTarget(
      @AuthenticationPrincipal CustomUser customUser,
      @RequestBody @Valid SetSalesTargetRequest request) {

    salesTargetCommandService.setSalesTarget(customUser.getShopId(), request);

    return ResponseEntity.ok().body(ApiResponse.success(null));
  }
}
