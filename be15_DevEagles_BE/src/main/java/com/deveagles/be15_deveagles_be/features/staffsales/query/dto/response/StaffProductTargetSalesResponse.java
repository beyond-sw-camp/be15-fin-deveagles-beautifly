package com.deveagles.be15_deveagles_be.features.staffsales.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffProductTargetSalesResponse {

  private String label;
  private Integer targetAmount;
  private Integer totalAmount;
  private Double achievementRate;
}
