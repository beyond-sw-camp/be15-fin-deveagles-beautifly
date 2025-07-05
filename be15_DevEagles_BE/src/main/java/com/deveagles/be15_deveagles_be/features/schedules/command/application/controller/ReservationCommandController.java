package com.deveagles.be15_deveagles_be.features.schedules.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.CreateReservationRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schedules/reservations")
@RequiredArgsConstructor
@Tag(name = "예약 생성", description = "고객 예약 등록 API")
public class ReservationCommandController {

  private final ReservationService reservationService;

  @Operation(
      summary = "예약 등록",
      description = "고객의 예약을 등록합니다. 예약 시간, 고객 ID, 담당자 ID 등 필수 정보를 포함해야 합니다.")
  @PostMapping
  public ApiResponse<Void> create(@RequestBody CreateReservationRequest request) {
    reservationService.createReservation(request);
    return ApiResponse.success(null);
  }
}
