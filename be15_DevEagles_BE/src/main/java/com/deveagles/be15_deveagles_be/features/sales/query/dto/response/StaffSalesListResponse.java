package com.deveagles.be15_deveagles_be.features.sales.query.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffSalesListResponse {

  private Long staffId;
  private String staffName;
  private List<StaffPaymentsSalesResponse> paymentsSalesList;
}
