package com.deveagles.be15_deveagles_be.features.staffsales.query.repository;

import com.deveagles.be15_deveagles_be.features.staffsales.query.dto.response.StaffPaymentsDetailSalesResponse;
import com.deveagles.be15_deveagles_be.features.staffsales.query.dto.response.StaffPaymentsSalesResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface StaffSalesQueryRepository {
  List<StaffPaymentsSalesResponse> getSalesByStaff(
      boolean isDetail, Long shopId, Long staffId, LocalDateTime startDate, LocalDateTime endDate);

  List<StaffPaymentsDetailSalesResponse> getDetailSalesByStaff(
      Long staffId, Long shopId, LocalDateTime startDate, LocalDateTime endDate);
}
