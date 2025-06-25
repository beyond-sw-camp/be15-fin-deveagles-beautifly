package com.deveagles.be15_deveagles_be.features.schedules.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.PlanDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.PlanListResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.RegularPlanDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.service.PlanQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules/plans")
@RequiredArgsConstructor
public class PlanQueryController {

  private final PlanQueryService planQueryService;

  // 단기 일정 상세 조회
  @GetMapping("/{planId}")
  public ResponseEntity<ApiResponse<PlanDetailResponse>> getPlanDetail(@PathVariable Long planId) {
    PlanDetailResponse response = planQueryService.getPlanDetail(planId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  // 정기 일정 상세 조회
  @GetMapping("/regular/{regularPlanId}")
  public ResponseEntity<ApiResponse<RegularPlanDetailResponse>> getRegularPlanDetail(
      @PathVariable Long regularPlanId) {
    RegularPlanDetailResponse response = planQueryService.getRegularPlanDetail(regularPlanId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  // 단기/정기 일정 목록 조회
  @GetMapping
  public ResponseEntity<ApiResponse<PagedResponse<PlanListResponse>>> getPlanList(
      @RequestParam(required = false) Long staffId,
      @RequestParam Long shopId,
      @RequestParam(defaultValue = "all") String planType,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    PagedResponse<PlanListResponse> response =
        planQueryService.getPlanList(staffId, shopId, planType, page, size);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
