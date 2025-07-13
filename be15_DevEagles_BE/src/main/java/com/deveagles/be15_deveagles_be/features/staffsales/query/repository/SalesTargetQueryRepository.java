package com.deveagles.be15_deveagles_be.features.staffsales.query.repository;

import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.SalesTarget;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

public interface SalesTargetQueryRepository {

  boolean existsTargetForShopInMonths(Long shopId, List<YearMonth> yearMonthList);

  List<SalesTarget> findAppliedTargets(Long shopId, Year year, Month month);

  int findTargetAmountByItemsOrMembership(Long shopId, Long staffId, boolean isItems, YearMonth ym);
}
