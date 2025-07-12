package com.deveagles.be15_deveagles_be.features.staffsales.query.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffSalesListResult {
  private List<StaffSalesListResponse> staffSalesList;
  private StaffSalesSummaryResponse totalSummary;
}
