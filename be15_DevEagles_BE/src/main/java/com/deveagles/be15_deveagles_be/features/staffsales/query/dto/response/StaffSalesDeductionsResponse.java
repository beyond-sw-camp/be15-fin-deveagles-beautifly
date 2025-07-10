package com.deveagles.be15_deveagles_be.features.staffsales.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffSalesDeductionsResponse {

  private String deduction;
  private Integer amount;
}
