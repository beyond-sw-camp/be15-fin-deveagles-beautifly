package com.deveagles.be15_deveagles_be.features.staffsales.query.repository;

import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.ProductType;
import java.time.LocalDate;

public interface SalesQueryRepository {
  int findTotalSales(
      Long shopId, Long staffId, ProductType type, LocalDate startDate, LocalDate endDate);
}
