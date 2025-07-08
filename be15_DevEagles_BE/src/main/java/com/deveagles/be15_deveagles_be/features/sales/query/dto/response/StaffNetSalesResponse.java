package com.deveagles.be15_deveagles_be.features.sales.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffNetSalesResponse {

  private String payments;
  private Integer amount;
}
