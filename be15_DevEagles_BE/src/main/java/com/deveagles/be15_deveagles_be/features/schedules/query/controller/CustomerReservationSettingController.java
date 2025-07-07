package com.deveagles.be15_deveagles_be.features.schedules.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.CustomerReservationSettingResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.service.ReservationSettingQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules/reservations")
@Tag(name = "예약 설정 조회", description = "고객용 예약 가능 설정 정보 조회 API")
public class CustomerReservationSettingController {

  private final ReservationSettingQueryService reservationSettingQueryService;

  @Operation(
      summary = "고객 예약 가능 설정 조회",
      description = "특정 매장의 지정 날짜에 해당하는 예약 가능 시간을 조회합니다. (근무시간, 점심시간 등 포함)")
  @GetMapping("/settings")
  public ApiResponse<CustomerReservationSettingResponse> getCustomerReservationSetting(
      @RequestParam Long shopId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    CustomerReservationSettingResponse response =
        reservationSettingQueryService.getReservationSetting(shopId, date);
    return ApiResponse.success(response);
  }
}
