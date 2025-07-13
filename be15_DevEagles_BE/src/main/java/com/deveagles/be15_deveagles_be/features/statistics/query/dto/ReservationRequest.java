package com.deveagles.be15_deveagles_be.features.statistics.query.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationRequest {
  private LocalDate startDate;
  private LocalDate endDate;
  private GroupBy groupBy;
  private TimeRange timeRange;
  private LocalTime startTime;
  private LocalTime endTime;
  private Boolean isPeakHour;
  private Long staffId;

  public enum GroupBy {
    DAY,
    WEEK,
    MONTH,
    TIME_SLOT,
    HOUR,
    STAFF,
    DAY_TIME_SLOT // 요일별/시간대별 히트맵용
  }

  public enum TimeRange {
    CUSTOM,
    LAST_WEEK,
    LAST_MONTH,
    LAST_6_MONTHS,
    LAST_YEAR
  }
}
