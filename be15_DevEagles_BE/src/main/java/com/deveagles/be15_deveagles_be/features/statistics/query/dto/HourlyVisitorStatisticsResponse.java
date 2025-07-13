package com.deveagles.be15_deveagles_be.features.statistics.query.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HourlyVisitorStatisticsResponse {
  private Integer hour;
  private String timeSlot;
  private Long maleVisitors;
  private Long femaleVisitors;
  private Long totalVisitors;
  private String displayTime;
}
