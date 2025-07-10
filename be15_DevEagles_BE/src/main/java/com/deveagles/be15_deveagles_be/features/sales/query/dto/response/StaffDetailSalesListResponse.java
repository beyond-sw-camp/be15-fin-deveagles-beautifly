package com.deveagles.be15_deveagles_be.features.sales.query.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffDetailSalesListResponse {

  private Long staffId;
  private String staffName;
  private List<StaffPaymentsDetailSalesResponse> paymentsDetailSalesList;
  private List<StaffPaymentsSalesResponse> paymentsSalesList;
  private StaffSalesSummaryResponse summary;
}
