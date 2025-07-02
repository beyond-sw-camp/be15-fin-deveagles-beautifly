package com.deveagles.be15_deveagles_be.features.schedules.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.CalendarScheduleRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.CalendarScheduleResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.service.CalendarScheduleQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules")
public class CalendarScheduleQueryController {

  private final CalendarScheduleQueryService calendarScheduleQueryService;

  @GetMapping("/calendar")
  public ApiResponse<List<CalendarScheduleResponse>> getSchedules(
      @AuthenticationPrincipal CustomUser user, CalendarScheduleRequest request) {
    Long shopId = user.getShopId();
    List<CalendarScheduleResponse> schedules =
        calendarScheduleQueryService.findSchedules(shopId, request);
    return ApiResponse.success(schedules);
  }
}
