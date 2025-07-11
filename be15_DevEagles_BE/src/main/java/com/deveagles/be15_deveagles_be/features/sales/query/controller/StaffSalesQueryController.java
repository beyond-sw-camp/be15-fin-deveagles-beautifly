package com.deveagles.be15_deveagles_be.features.sales.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.request.GetStaffSalesListRequest;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.StaffSalesDetailListResult;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.StaffSalesListResult;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.StaffSalesTargetListResult;
import com.deveagles.be15_deveagles_be.features.sales.query.service.StaffSalesQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
public class StaffSalesQueryController {

  private final StaffSalesQueryService staffSalesQueryService;

  @GetMapping()
  @Operation(summary = "직원별 결산 조회", description = "조회 기간(월별 / 기간별)에 따라 직원별 결산 내역을 조회합니다.")
  public ResponseEntity<ApiResponse<StaffSalesListResult>> getStaffSales(
      @AuthenticationPrincipal CustomUser customUser,
      @ModelAttribute GetStaffSalesListRequest request) {

    StaffSalesListResult response =
        staffSalesQueryService.getStaffSales(customUser.getShopId(), request);

    return ResponseEntity.ok().body(ApiResponse.success(response));
  }

  @GetMapping("/details")
  @Operation(summary = "직원별 상세 결산 조회", description = "조회기간(월별 / 기간별)에 따라 직원별 상세 결산 내역을 조회합니다.")
  public ResponseEntity<ApiResponse<StaffSalesDetailListResult>> getStaffDetailSales(
      @AuthenticationPrincipal CustomUser customUser,
      @ModelAttribute GetStaffSalesListRequest request) {

    StaffSalesDetailListResult response =
        staffSalesQueryService.getStaffDetailSales(customUser.getShopId(), request);

    return ResponseEntity.ok().body(ApiResponse.success(response));
  }

  @GetMapping("/targets")
  @Operation(summary = "직원별 목표 매출 결산 조회", description = "조회기간(월별 / 기간별)에 따라 직원별 목표 매출 결산을 조회합니다.")
  public ResponseEntity<ApiResponse<StaffSalesTargetListResult>> getStaffSalesTarget(
      @AuthenticationPrincipal CustomUser customUser,
      @ModelAttribute GetStaffSalesListRequest request) {

    StaffSalesTargetListResult response =
        staffSalesQueryService.getStaffSalesTarget(customUser.getShopId(), request);

    return ResponseEntity.ok().body(ApiResponse.success(response));
  }
}
