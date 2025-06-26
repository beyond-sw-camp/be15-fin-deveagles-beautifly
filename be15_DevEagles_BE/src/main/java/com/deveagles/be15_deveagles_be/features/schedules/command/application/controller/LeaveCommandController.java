package com.deveagles.be15_deveagles_be.features.schedules.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.*;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.service.LeaveCommandService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class LeaveCommandController {

  private final LeaveCommandService leaveCommandService;

  // 단기 휴무 등록
  @PostMapping("/leaves")
  public ResponseEntity<ApiResponse<Long>> createLeave(@RequestBody CreateLeaveRequest request) {
    Long id = leaveCommandService.createLeave(request);
    return ResponseEntity.ok(ApiResponse.success(id));
  }

  // 단기 휴무 수정
  @PutMapping("/leaves/{leaveId}")
  public ResponseEntity<ApiResponse<Void>> updateLeave(
      @PathVariable Long leaveId, @RequestBody UpdateLeaveRequest request) {
    leaveCommandService.updateLeave(leaveId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  // 정기 휴무 등록
  @PostMapping("/regular-leaves")
  public ResponseEntity<ApiResponse<Long>> createRegularLeave(
      @RequestBody CreateRegularLeaveRequest request) {
    Long id = leaveCommandService.createRegularLeave(request);
    return ResponseEntity.ok(ApiResponse.success(id));
  }

  // 정기 휴무 수정
  @PutMapping("/regular-leaves/{regularLeaveId}")
  public ResponseEntity<ApiResponse<Void>> updateRegularLeave(
      @PathVariable Long regularLeaveId, @RequestBody UpdateRegularLeaveRequest request) {
    leaveCommandService.updateRegularLeave(regularLeaveId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  // 단기 + 정기 휴무 통합 삭제
  @DeleteMapping("/leaves")
  public ResponseEntity<ApiResponse<Void>> deleteSchedules(
      @RequestBody List<DeleteScheduleRequest> requests) {
    leaveCommandService.deleteMixedLeaves(requests);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  // 정기 <-> 단기 switch
  @PostMapping("/leaves/switch")
  public ResponseEntity<ApiResponse<Void>> switchSchedule(
      @RequestBody UpdateLeaveScheduleRequest request) {
    leaveCommandService.switchSchedule(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
