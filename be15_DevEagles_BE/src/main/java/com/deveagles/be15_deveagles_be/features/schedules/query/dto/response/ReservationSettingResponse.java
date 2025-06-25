package com.deveagles.be15_deveagles_be.features.schedules.query.dto.response;

import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReservationSettingResponse {
  private Long shopId;
  private Integer availableDay;
  private LocalTime availableStartTime;
  private LocalTime availableEndTime;
  private LocalTime lunchStartTime;
  private LocalTime lunchEndTime;
  private Integer reservationUnitMinutes;
}
