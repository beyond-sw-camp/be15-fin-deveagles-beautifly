package com.deveagles.be15_deveagles_be.features.staffsales.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response.IncentiveListResult;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.service.IncentiveCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/staff-sales")
@Tag(name = "직원 결산 관리", description = "직원 결산 관리 관련 API")
public class StaffSalesCommandController {

  private final IncentiveCommandService incentiveCommandService;

  @GetMapping("/incentive")
  @Operation(summary = "직원 인센티브 조회", description = "매장 별 직원에게 적용하는 인센티브 내역을 조회합니다.")
  public ResponseEntity<ApiResponse<IncentiveListResult>> getIncentives(
      @AuthenticationPrincipal CustomUser customUser) {

    IncentiveListResult response = incentiveCommandService.getIncentives(customUser.getShopId());

    return ResponseEntity.ok().body(ApiResponse.success(response));
  }
}
