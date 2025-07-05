package com.deveagles.be15_deveagles_be.features.schedules.query.controller;

import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.ReservationSettingResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.service.ReservationSettingQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/schedules/reservation/settings")
@RequiredArgsConstructor
@Tag(name = "예약 설정 조회", description = "매장별 요일 예약 설정 조회 API")
public class ReservationSettingQueryController {

  private final ReservationSettingQueryService reservationSettingQueryService;

  @Operation(summary = "매장 예약 설정 조회", description = "매장 ID에 해당하는 날짜의 예약 설정 정보를 조회합니다.")
  @GetMapping("/{shopId}")
  public List<ReservationSettingResponse> getSettings(@PathVariable Long shopId) {
    return reservationSettingQueryService.getReservationSettings(shopId);
  }
}
