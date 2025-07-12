package com.deveagles.be15_deveagles_be.features.staffsales.query.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffSalesTargetResponse {

  private Long staffId;
  private String staffName;
  private List<StaffProductTargetSalesResponse> targetSalesList;

  private Integer totalTargetAmount;
  private Integer totalActualAmount;
  private Double totalAchievementRate;
}
