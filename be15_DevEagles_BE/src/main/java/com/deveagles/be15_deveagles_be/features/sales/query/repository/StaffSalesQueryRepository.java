package com.deveagles.be15_deveagles_be.features.sales.query.repository;

import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.StaffPaymentsSalesResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface StaffSalesQueryRepository {
  List<StaffPaymentsSalesResponse> getSalesByStaff(
      Long staffId, LocalDateTime startDate, LocalDateTime endDate);
}
