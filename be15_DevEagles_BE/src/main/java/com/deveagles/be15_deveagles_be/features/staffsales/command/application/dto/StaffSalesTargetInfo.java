package com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffSalesTargetInfo {

  private Long staffId;
  private Map<String, Integer> targetAmounts;
}
