package com.deveagles.be15_deveagles_be.features.schedules.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.LeaveListRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.LeaveDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.LeaveListResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.RegularLeaveDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.service.LeaveQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules/leaves")
public class LeaveQueryController {

  private final LeaveQueryService leaveQueryService;

  @GetMapping
  public ApiResponse<PagedResponse<LeaveListResponse>> getLeaveList(LeaveListRequest request) {
    return ApiResponse.success(PagedResponse.from(leaveQueryService.getLeaveList(request)));
  }

  @GetMapping("/{leaveId}")
  public ApiResponse<LeaveDetailResponse> getLeaveDetail(@PathVariable Long leaveId) {
    LeaveDetailResponse response = leaveQueryService.getLeaveDetail(leaveId);
    return ApiResponse.success(response);
  }

  @GetMapping("/regular/{regularLeaveId}")
  public ApiResponse<RegularLeaveDetailResponse> getRegularLeaveDetail(
      @PathVariable Long regularLeaveId) {
    RegularLeaveDetailResponse response = leaveQueryService.getRegularLeaveDetail(regularLeaveId);
    return ApiResponse.success(response);
  }
}
