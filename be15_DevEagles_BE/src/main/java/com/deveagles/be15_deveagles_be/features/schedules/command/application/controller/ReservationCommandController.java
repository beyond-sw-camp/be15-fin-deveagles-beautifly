package com.deveagles.be15_deveagles_be.features.schedules.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.CreateReservationFullRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.CreateReservationRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.UpdateReservationRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.UpdateReservationStatusRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules/reservations")
@RequiredArgsConstructor
@Tag(name = "예약 생성", description = "고객 예약 등록 API")
public class ReservationCommandController {

  private final ReservationService reservationService;

  @Operation(summary = "예약 등록 (고객 등록)", description = "고객이 예약하면 예약을 등록합니다.")
  @PostMapping
  public ApiResponse<Long> create(@RequestBody CreateReservationRequest request) {
    Long reservationId = reservationService.createReservation(request);
    return ApiResponse.success(reservationId);
  }

  @Operation(summary = "예약 등록 (매장 등록)", description = "매장에서 예약을 등록합니다.")
  @PostMapping("/shop")
  public ApiResponse<Long> createFull(@RequestBody CreateReservationFullRequest request) {
    Long reservationId = reservationService.createFullReservation(request);
    return ApiResponse.success(reservationId);
  }

  @Operation(summary = "예약 수정", description = "기존 예약을 수정합니다.")
  @PutMapping("/{reservationId}")
  public ApiResponse<Void> updateReservation(
      @PathVariable Long reservationId, @RequestBody UpdateReservationRequest request) {
    reservationService.updateReservation(reservationId, request);
    return ApiResponse.success(null);
  }

  @Operation(summary = "예약 단건 삭제", description = "예약 ID로 단건 삭제합니다.")
  @DeleteMapping("/{reservationId}")
  public ApiResponse<Void> deleteReservation(@PathVariable Long reservationId) {
    reservationService.deleteReservation(reservationId);
    return ApiResponse.success(null);
  }

  @Operation(summary = "예약 상태 변경", description = "예약 상태를 변경합니다.")
  @PutMapping("/{reservationId}/status")
  public ApiResponse<Void> changeReservationStatus(
      @PathVariable Long reservationId, @RequestBody UpdateReservationStatusRequest request) {
    reservationService.changeReservationStatus(reservationId, request.reservationStatusName());
    return ApiResponse.success(null);
  }
}
