package com.deveagles.be15_deveagles_be.features.staffsales.query.dto.response;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StaffNetSalesResponse {
  private PaymentsMethod paymentsMethod;
  private Integer amount;
  private Integer incentiveAmount;
}
