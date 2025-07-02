package com.deveagles.be15_deveagles_be.features.schedules.command.application.controller;

import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.UpdateReservationSettingRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.service.ReservationSettingCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules/reservation/settings")
@Tag(name = "예약 설정 수정", description = "매장의 요일별 예약 가능 설정 수정 API")
public class ReservationSettingCommandController {

  private final ReservationSettingCommandService reservationSettingCommandService;

  @Operation(summary = "예약 설정 수정", description = "매장의 요일별 예약 가능 설정을 수정합니다. 기존 값을 덮어씁니다.")
  @PutMapping("/{shopId}")
  public ResponseEntity<Void> updateReservationSettings(
      @PathVariable Long shopId, @RequestBody List<UpdateReservationSettingRequest> requestList) {
    reservationSettingCommandService.updateReservationSettings(shopId, requestList);
    return ResponseEntity.noContent().build();
  }
}
