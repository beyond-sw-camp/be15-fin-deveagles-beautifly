package com.deveagles.be15_deveagles_be.features.schedules.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.*;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.service.PlanCommandService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class PlanCommandController {

  private final PlanCommandService planCommandService;

  // 단기 일정 저장
  @PostMapping("/plans")
  public ResponseEntity<ApiResponse<Long>> createPlan(@RequestBody CreatePlanRequest request) {
    Long id = planCommandService.createPlan(request);
    return ResponseEntity.ok(ApiResponse.success(id));
  }

  // 정기 일정 저장
  @PostMapping("/regular-plans")
  public ResponseEntity<ApiResponse<Long>> createRegularPlan(
      @RequestBody CreateRegularPlanRequest request) {
    Long id = planCommandService.createRegularPlan(request);
    return ResponseEntity.ok(ApiResponse.success(id));
  }

  // 단기 일정 수정
  @PutMapping("/plans/{planId}")
  public ResponseEntity<ApiResponse<Void>> updatePlan(
      @PathVariable Long planId, @RequestBody CreatePlanRequest request) {
    planCommandService.updatePlan(planId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  // 정기 일정 수정
  @PutMapping("/regular-plans/{regularPlanId}")
  public ResponseEntity<ApiResponse<Void>> updateRegularPlan(
      @PathVariable Long regularPlanId, @RequestBody CreateRegularPlanRequest request) {
    planCommandService.updateRegularPlan(regularPlanId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  // 단기 <-> 정기 전환
  @PostMapping("/plans/switch")
  public ResponseEntity<ApiResponse<Void>> switchSchedule(
      @RequestBody UpdateScheduleRequest request) {
    planCommandService.switchSchedule(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  // 일정 다건 삭제
  @DeleteMapping("/plans")
  public ResponseEntity<ApiResponse<Void>> deleteSchedules(
      @RequestBody List<DeleteScheduleRequest> requests) {
    planCommandService.deleteMixedSchedules(requests);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
