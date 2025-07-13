package com.deveagles.be15_deveagles_be.features.statistics.query.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyVisitorStatisticsResponse {
  private LocalDate date;
  private String dayOfWeek;
  private Long maleVisitors;
  private Long femaleVisitors;
  private Long totalVisitors;
  private String displayDate;
}
