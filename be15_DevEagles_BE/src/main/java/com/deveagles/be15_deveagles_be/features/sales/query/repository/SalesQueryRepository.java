package com.deveagles.be15_deveagles_be.features.sales.query.repository;

import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.ProductType;
import java.time.LocalDate;

public interface SalesQueryRepository {
  int findTotalSales(
      Long shopId, Long staffId, ProductType type, LocalDate startDate, LocalDate endDate);
}
