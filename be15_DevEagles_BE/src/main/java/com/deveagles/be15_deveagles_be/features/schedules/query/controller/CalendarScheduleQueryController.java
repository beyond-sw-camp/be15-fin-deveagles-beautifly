package com.deveagles.be15_deveagles_be.features.schedules.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.CalendarRegularRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.CalendarScheduleRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.CalendarRenderedResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.CalendarScheduleResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.service.CalendarScheduleQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
@Tag(name = "일정 캘린더", description = "예약/일정/휴무 조회 API")
public class CalendarScheduleQueryController {

  private final CalendarScheduleQueryService calendarScheduleQueryService;

  @GetMapping("/calendar")
  @Operation(summary = "예약/일정/휴무 전체 조회", description = "지정된 기간과 조건에 따라 예약, 단기 일정, 단기 휴무를 조회합니다.")
  public ApiResponse<List<CalendarScheduleResponse>> getSchedules(
      @AuthenticationPrincipal CustomUser user, CalendarScheduleRequest request) {
    Long shopId = user.getShopId();
    List<CalendarScheduleResponse> schedules =
        calendarScheduleQueryService.findSchedules(shopId, request);
    return ApiResponse.success(schedules);
  }

  @GetMapping("/calendar/regular")
  @Operation(summary = "정기 일정/휴무 조회", description = "정기 일정 및 정기 휴무를 주어진 기간에 맞춰 확장하여 조회합니다.")
  public ApiResponse<List<CalendarRenderedResponse>> expandRegularSchedules(
      @AuthenticationPrincipal CustomUser user, @ModelAttribute CalendarRegularRequest request) {
    Long shopId = user.getShopId();

    List<CalendarRenderedResponse> schedules =
        calendarScheduleQueryService.getExpandedRegularSchedules(shopId, request);

    return ApiResponse.success(schedules);
  }
}
