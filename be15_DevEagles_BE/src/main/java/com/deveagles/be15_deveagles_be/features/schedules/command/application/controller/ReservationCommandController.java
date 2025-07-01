package com.deveagles.be15_deveagles_be.features.schedules.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.CreateReservationRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schedules/reservations")
@RequiredArgsConstructor
public class ReservationCommandController {

  private final ReservationService reservationService;

  @PostMapping
  public ApiResponse<Void> create(@RequestBody CreateReservationRequest request) {
    reservationService.createReservation(request);
    return ApiResponse.success(null);
  }
}
