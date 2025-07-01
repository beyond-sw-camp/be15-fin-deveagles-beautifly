package com.deveagles.be15_deveagles_be.features.schedules.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.CustomerReservationSettingResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.service.ReservationSettingQueryService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules/reservations")
public class CustomerReservationSettingController {

  private final ReservationSettingQueryService reservationSettingQueryService;

  @GetMapping("/settings")
  public ApiResponse<CustomerReservationSettingResponse> getCustomerReservationSetting(
      @RequestParam Long shopId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    CustomerReservationSettingResponse response =
        reservationSettingQueryService.getReservationSetting(shopId, date);
    return ApiResponse.success(response);
  }
}
