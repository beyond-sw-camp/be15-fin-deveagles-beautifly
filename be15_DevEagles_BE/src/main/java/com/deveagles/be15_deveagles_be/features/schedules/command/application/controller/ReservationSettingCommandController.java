package com.deveagles.be15_deveagles_be.features.schedules.command.application.controller;

import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.UpdateReservationSettingRequest;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.service.ReservationSettingCommandService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules/reservation/settings")
public class ReservationSettingCommandController {

  private final ReservationSettingCommandService reservationSettingCommandService;

  @PutMapping("/{shopId}")
  public ResponseEntity<Void> updateReservationSettings(
      @PathVariable Long shopId, @RequestBody List<UpdateReservationSettingRequest> requestList) {
    reservationSettingCommandService.updateReservationSettings(shopId, requestList);
    return ResponseEntity.noContent().build();
  }
}
