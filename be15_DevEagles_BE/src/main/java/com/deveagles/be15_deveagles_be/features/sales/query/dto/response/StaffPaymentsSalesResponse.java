package com.deveagles.be15_deveagles_be.features.sales.query.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffPaymentsSalesResponse {

  private String category;
  private List<StaffNetSalesResponse> netSalesList;
  private List<StaffSalesDeductionsResponse> deductionList;
}
