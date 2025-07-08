package com.deveagles.be15_deveagles_be.features.schedules.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.*;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.service.PlanCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
@Tag(name = "일정 관리", description = "단기 및 정기 일정 생성, 수정, 삭제 API")
public class PlanCommandController {

  private final PlanCommandService planCommandService;

  @Operation(summary = "단기 일정 생성", description = "단기 일정을 새로 등록합니다.")
  @PostMapping("/plans")
  public ResponseEntity<ApiResponse<Long>> createPlan(
      @AuthenticationPrincipal CustomUser user, @RequestBody CreatePlanRequest request) {
    Long id = planCommandService.createPlan(user.getShopId(), request);
    return ResponseEntity.ok(ApiResponse.success(id));
  }

  @Operation(summary = "정기 일정 생성", description = "정기 일정을 새로 등록합니다.")
  @PostMapping("/regular-plans")
  public ResponseEntity<ApiResponse<Long>> createRegularPlan(
      @AuthenticationPrincipal CustomUser user, @RequestBody CreateRegularPlanRequest request) {
    Long id = planCommandService.createRegularPlan(user.getShopId(), request);
    return ResponseEntity.ok(ApiResponse.success(id));
  }

  @Operation(summary = "단기 일정 수정", description = "기존 단기 일정을 수정합니다.")
  @PutMapping("/plans/{planId}")
  public ResponseEntity<ApiResponse<Void>> updatePlan(
      @AuthenticationPrincipal CustomUser user,
      @PathVariable Long planId,
      @RequestBody CreatePlanRequest request) {
    planCommandService.updatePlan(user.getShopId(), planId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "정기 일정 수정", description = "기존 정기 일정을 수정합니다.")
  @PutMapping("/regular-plans/{regularPlanId}")
  public ResponseEntity<ApiResponse<Void>> updateRegularPlan(
      @AuthenticationPrincipal CustomUser user,
      @PathVariable Long regularPlanId,
      @RequestBody CreateRegularPlanRequest request) {
    planCommandService.updateRegularPlan(user.getShopId(), regularPlanId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "일정 타입 전환", description = "단기 ↔ 정기 일정으로 전환합니다.")
  @PostMapping("/plans/switch")
  public ResponseEntity<ApiResponse<Void>> switchSchedule(
      @AuthenticationPrincipal CustomUser user, @RequestBody UpdatePlanScheduleRequest request) {
    planCommandService.switchSchedule(user.getShopId(), request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "일정 삭제 (다건)", description = "단기 및 정기 일정을 함께 삭제합니다.")
  @DeleteMapping("/plans")
  public ResponseEntity<ApiResponse<Void>> deleteSchedules(
      @AuthenticationPrincipal CustomUser user, @RequestBody List<DeleteScheduleRequest> requests) {
    planCommandService.deleteMixedSchedules(user.getShopId(), requests);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
