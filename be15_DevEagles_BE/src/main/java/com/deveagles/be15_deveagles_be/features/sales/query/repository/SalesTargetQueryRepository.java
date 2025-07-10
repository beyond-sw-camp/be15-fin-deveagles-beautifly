package com.deveagles.be15_deveagles_be.features.sales.query.repository;

import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.ProductType;
import java.time.YearMonth;
import java.util.List;

public interface SalesTargetQueryRepository {
  int findTargetAmount(Long shopId, Long staffId, ProductType type, YearMonth from);

  boolean existsTargetForShopInMonths(Long shopId, List<YearMonth> yearMonthList);
}
