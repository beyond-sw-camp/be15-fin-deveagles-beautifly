package com.deveagles.be15_deveagles_be.features.schedules.query.controller;

import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.ReservationSettingResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.service.ReservationSettingQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/schedules/reservation/settings")
@RequiredArgsConstructor
public class ReservationSettingQueryController {

  private final ReservationSettingQueryService reservationSettingQueryService;

  @GetMapping("/{shopId}")
  public List<ReservationSettingResponse> getSettings(@PathVariable Long shopId) {
    return reservationSettingQueryService.getReservationSettings(shopId);
  }
}
