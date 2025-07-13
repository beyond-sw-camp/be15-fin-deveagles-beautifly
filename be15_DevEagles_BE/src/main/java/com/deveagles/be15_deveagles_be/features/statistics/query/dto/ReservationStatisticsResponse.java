package com.deveagles.be15_deveagles_be.features.statistics.query.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationStatisticsResponse {
  private LocalDate date;
  private LocalTime timeSlot;
  private Integer totalSlots;
  private Integer reservedSlots;
  private Integer availableSlots;
  private BigDecimal reservationRate;
  private String groupBy;
  private String displayKey;
  private Long staffId;
  private String staffName;
  private String dayOfWeek; // 요일 정보 (MON, TUE, WED, THU, FRI, SAT, SUN)

  // 하위 호환성을 위한 생성자 (dayOfWeek 없이)
  public ReservationStatisticsResponse(
      LocalDate date,
      LocalTime timeSlot,
      Integer totalSlots,
      Integer reservedSlots,
      Integer availableSlots,
      BigDecimal reservationRate,
      String groupBy,
      String displayKey,
      Long staffId,
      String staffName) {
    this.date = date;
    this.timeSlot = timeSlot;
    this.totalSlots = totalSlots;
    this.reservedSlots = reservedSlots;
    this.availableSlots = availableSlots;
    this.reservationRate = reservationRate;
    this.groupBy = groupBy;
    this.displayKey = displayKey;
    this.staffId = staffId;
    this.staffName = staffName;
    this.dayOfWeek = null; // 기본값
  }
}
