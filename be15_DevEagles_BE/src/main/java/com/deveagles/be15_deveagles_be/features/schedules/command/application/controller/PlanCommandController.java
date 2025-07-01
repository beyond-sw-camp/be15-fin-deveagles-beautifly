package com.deveagles.be15_deveagles_be.features.schedules.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.*;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.service.PlanCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
@Tag(name = "일정 관리", description = "단기 및 정기 일정 생성, 수정, 삭제 API")
public class PlanCommandController {

  private final PlanCommandService planCommandService;

  @Operation(summary = "단기 일정 생성", description = "단기 일정을 새로 등록합니다.")
  @PostMapping("/plans")
  public ResponseEntity<ApiResponse<Long>> createPlan(@RequestBody CreatePlanRequest request) {
    Long id = planCommandService.createPlan(request);
    return ResponseEntity.ok(ApiResponse.success(id));
  }

  @Operation(summary = "정기 일정 생성", description = "정기 일정을 새로 등록합니다.")
  @PostMapping("/regular-plans")
  public ResponseEntity<ApiResponse<Long>> createRegularPlan(
      @RequestBody CreateRegularPlanRequest request) {
    Long id = planCommandService.createRegularPlan(request);
    return ResponseEntity.ok(ApiResponse.success(id));
  }

  @Operation(summary = "단기 일정 수정", description = "기존 단기 일정을 수정합니다.")
  @PutMapping("/plans/{planId}")
  public ResponseEntity<ApiResponse<Void>> updatePlan(
      @PathVariable Long planId, @RequestBody CreatePlanRequest request) {
    planCommandService.updatePlan(planId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "정기 일정 수정", description = "기존 정기 일정을 수정합니다.")
  @PutMapping("/regular-plans/{regularPlanId}")
  public ResponseEntity<ApiResponse<Void>> updateRegularPlan(
      @PathVariable Long regularPlanId, @RequestBody CreateRegularPlanRequest request) {
    planCommandService.updateRegularPlan(regularPlanId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "일정 타입 전환", description = "단기 ↔ 정기 일정으로 전환합니다.")
  @PostMapping("/plans/switch")
  public ResponseEntity<ApiResponse<Void>> switchSchedule(
      @RequestBody UpdatePlanScheduleRequest request) {
    planCommandService.switchSchedule(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "일정 삭제 (다건)", description = "단기 및 정기 일정을 함께 삭제합니다.")
  @DeleteMapping("/plans")
  public ResponseEntity<ApiResponse<Void>> deleteSchedules(
      @RequestBody List<DeleteScheduleRequest> requests) {
    planCommandService.deleteMixedSchedules(requests);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
