package com.deveagles.be15_deveagles_be.features.schedules.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.*;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.service.LeaveCommandService;
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
@Tag(name = "휴무 관리", description = "직원 단기 및 정기 휴무 등록, 수정, 삭제 API")
public class LeaveCommandController {

  private final LeaveCommandService leaveCommandService;

  @Operation(summary = "단기 휴무 등록", description = "직원의 단기 휴무 일정을 등록합니다.")
  @PostMapping("/leaves")
  public ResponseEntity<ApiResponse<Long>> createLeave(
      @AuthenticationPrincipal CustomUser user, @RequestBody CreateLeaveRequest request) {
    Long id = leaveCommandService.createLeave(user.getShopId(), request);
    return ResponseEntity.ok(ApiResponse.success(id));
  }

  @Operation(summary = "단기 휴무 수정", description = "기존 단기 휴무 일정을 수정합니다.")
  @PutMapping("/leaves/{leaveId}")
  public ResponseEntity<ApiResponse<Void>> updateLeave(
      @AuthenticationPrincipal CustomUser user,
      @PathVariable Long leaveId,
      @RequestBody UpdateLeaveRequest request) {
    leaveCommandService.updateLeave(user.getShopId(), leaveId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "정기 휴무 등록", description = "직원의 정기 휴무 일정을 등록합니다.")
  @PostMapping("/regular-leaves")
  public ResponseEntity<ApiResponse<Long>> createRegularLeave(
      @AuthenticationPrincipal CustomUser user, @RequestBody CreateRegularLeaveRequest request) {
    Long id = leaveCommandService.createRegularLeave(user.getShopId(), request);
    return ResponseEntity.ok(ApiResponse.success(id));
  }

  @Operation(summary = "정기 휴무 수정", description = "기존 정기 휴무 일정을 수정합니다.")
  @PutMapping("/regular-leaves/{regularLeaveId}")
  public ResponseEntity<ApiResponse<Void>> updateRegularLeave(
      @AuthenticationPrincipal CustomUser user,
      @PathVariable Long regularLeaveId,
      @RequestBody UpdateRegularLeaveRequest request) {
    leaveCommandService.updateRegularLeave(user.getShopId(), regularLeaveId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "휴무 삭제 (다건)", description = "단기 및 정기 휴무를 함께 삭제합니다.")
  @DeleteMapping("/leaves")
  public ResponseEntity<ApiResponse<Void>> deleteSchedules(
      @AuthenticationPrincipal CustomUser user, @RequestBody List<DeleteScheduleRequest> requests) {
    leaveCommandService.deleteMixedLeaves(user.getShopId(), requests);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "휴무 일정 타입 전환", description = "정기 ↔ 단기 휴무로 일정 타입을 전환합니다.")
  @PostMapping("/leaves/switch")
  public ResponseEntity<ApiResponse<Void>> switchSchedule(
      @AuthenticationPrincipal CustomUser user, @RequestBody UpdateLeaveScheduleRequest request) {
    leaveCommandService.switchSchedule(user.getShopId(), request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
