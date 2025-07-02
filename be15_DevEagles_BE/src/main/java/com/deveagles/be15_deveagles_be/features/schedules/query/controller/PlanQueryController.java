package com.deveagles.be15_deveagles_be.features.schedules.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.PlanDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.PlanListResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.RegularPlanDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.service.PlanQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schedules/plans")
@RequiredArgsConstructor
@Tag(name = "일정 조회", description = "직원 단기/정기 일정 상세 및 목록 조회 API")
public class PlanQueryController {

  private final PlanQueryService planQueryService;

  @Operation(summary = "단기 일정 상세 조회", description = "단기 일정 ID로 상세 정보를 조회합니다.")
  @GetMapping("/{planId}")
  public ResponseEntity<ApiResponse<PlanDetailResponse>> getPlanDetail(@PathVariable Long planId) {
    PlanDetailResponse response = planQueryService.getPlanDetail(planId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "정기 일정 상세 조회", description = "정기 일정 ID로 상세 정보를 조회합니다.")
  @GetMapping("/regular/{regularPlanId}")
  public ResponseEntity<ApiResponse<RegularPlanDetailResponse>> getRegularPlanDetail(
      @PathVariable Long regularPlanId) {
    RegularPlanDetailResponse response = planQueryService.getRegularPlanDetail(regularPlanId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }

  @Operation(summary = "일정 목록 조회", description = "단기/정기 일정을 통합하여 조회합니다. 필터: 담당자, 일정 타입, 기간, 페이징")
  @GetMapping
  public ResponseEntity<ApiResponse<PagedResponse<PlanListResponse>>> getPlanList(
      @RequestParam(required = false) Long staffId,
      @RequestParam Long shopId,
      @RequestParam(defaultValue = "all") String planType,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : null;
    LocalDateTime toDateTime = to != null ? to.atTime(LocalTime.MAX) : null;

    var result =
        planQueryService.getPlanList(
            staffId, shopId, planType, fromDateTime, toDateTime, page, size);

    return ResponseEntity.ok(ApiResponse.success(result));
  }
}
