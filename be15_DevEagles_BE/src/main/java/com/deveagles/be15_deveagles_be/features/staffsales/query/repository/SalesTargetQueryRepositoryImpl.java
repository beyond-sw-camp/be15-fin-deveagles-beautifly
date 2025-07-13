package com.deveagles.be15_deveagles_be.features.staffsales.query.repository;

import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.QSalesTarget;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.SalesTarget;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SalesTargetQueryRepositoryImpl implements SalesTargetQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public boolean existsTargetForShopInMonths(Long shopId, List<YearMonth> yearMonthList) {
    QSalesTarget q = QSalesTarget.salesTarget;

    BooleanExpression shopCondition = q.shopId.eq(shopId);

    BooleanBuilder dateCondition = new BooleanBuilder();
    for (YearMonth ym : yearMonthList) {
      dateCondition.or(q.targetYear.eq(ym.getYear()).and(q.targetMonth.eq(ym.getMonthValue())));
    }

    Integer fetch =
        queryFactory.selectOne().from(q).where(shopCondition.and(dateCondition)).fetchFirst();

    return fetch != null;
  }

  @Override
  public List<SalesTarget> findAppliedTargets(Long shopId, Year year, Month month) {

    QSalesTarget q = QSalesTarget.salesTarget;

    return queryFactory
        .selectFrom(q)
        .where(
            q.shopId.eq(shopId),
            q.targetYear.eq(year.getValue()),
            q.targetMonth.eq(month.getValue()),
            q.applyStatus.isTrue())
        .fetch();
  }

  @Override
  public int findTargetAmountByItemsOrMembership(
      Long shopId, Long staffId, boolean isItems, YearMonth yearMonth) {
    QSalesTarget q = QSalesTarget.salesTarget;

    BooleanExpression baseCondition =
        q.shopId
            .eq(shopId)
            .and(q.staffId.eq(staffId))
            .and(q.targetYear.eq(yearMonth.getYear()))
            .and(q.targetMonth.eq(yearMonth.getMonthValue()));

    BooleanExpression typeCondition = isItems ? q.items.isTrue() : q.membership.isTrue();

    Integer result =
        queryFactory.select(q.sales).from(q).where(baseCondition.and(typeCondition)).fetchOne();

    // fallback: 개별 설정 없을 경우 일괄 설정 조회
    if (result == null) {
      BooleanExpression bulkCondition =
          q.shopId
              .eq(shopId)
              .and(q.staffId.isNull()) // BULK
              .and(q.targetYear.eq(yearMonth.getYear()))
              .and(q.targetMonth.eq(yearMonth.getMonthValue()))
              .and(q.applyStatus.isTrue());

      result =
          queryFactory.select(q.sales).from(q).where(bulkCondition.and(typeCondition)).fetchOne();
    }

    return result != null ? result : 0;
  }
}
