package com.deveagles.be15_deveagles_be.features.statistics.query.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSummaryResponse {
  private Integer totalSlots;
  private Integer totalReservedSlots;
  private Integer totalAvailableSlots;
  private BigDecimal averageReservationRate;
  private Integer totalDays;
  private BigDecimal dailyAverageReservations;
  private Integer peakHourSlots;
  private Integer offPeakHourSlots;
  private BigDecimal peakHourReservationRate;
  private BigDecimal offPeakHourReservationRate;
}
