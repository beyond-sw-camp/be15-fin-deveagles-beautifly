package com.deveagles.be15_deveagles_be.features.schedules.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.LeaveListRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.LeaveDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.LeaveListResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.RegularLeaveDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.service.LeaveQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules/leaves")
@Tag(name = "휴무 조회", description = "직원 단기/정기 휴무 조회 API")
public class LeaveQueryController {

  private final LeaveQueryService leaveQueryService;

  @Operation(
      summary = "휴무 목록 조회",
      description = "단기/정기 휴무를 통합하여 페이징으로 조회합니다. 필터: 직원 ID, 휴무 타입, 기간.")
  @GetMapping
  public ApiResponse<PagedResponse<LeaveListResponse>> getLeaveList(LeaveListRequest request) {
    return ApiResponse.success(PagedResponse.from(leaveQueryService.getLeaveList(request)));
  }

  @Operation(summary = "단기 휴무 상세 조회", description = "단기 휴무 ID를 기준으로 상세 정보를 조회합니다.")
  @GetMapping("/{leaveId}")
  public ApiResponse<LeaveDetailResponse> getLeaveDetail(@PathVariable Long leaveId) {
    LeaveDetailResponse response = leaveQueryService.getLeaveDetail(leaveId);
    return ApiResponse.success(response);
  }

  @Operation(summary = "정기 휴무 상세 조회", description = "정기 휴무 ID를 기준으로 상세 정보를 조회합니다.")
  @GetMapping("/regular/{regularLeaveId}")
  public ApiResponse<RegularLeaveDetailResponse> getRegularLeaveDetail(
      @PathVariable Long regularLeaveId) {
    RegularLeaveDetailResponse response = leaveQueryService.getRegularLeaveDetail(regularLeaveId);
    return ApiResponse.success(response);
  }
}
