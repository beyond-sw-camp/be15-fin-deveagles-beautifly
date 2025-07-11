package com.deveagles.be15_deveagles_be.features.sales.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffSalesSummaryResponse {

  private int totalNetSales;
  private int totalDiscount;
  private int totalPrepaid;
  private int totalCoupon;
  private int totalIncentiveAmount;
}
