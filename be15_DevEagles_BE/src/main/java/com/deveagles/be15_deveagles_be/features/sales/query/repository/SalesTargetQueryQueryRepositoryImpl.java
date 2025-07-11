package com.deveagles.be15_deveagles_be.features.sales.query.repository;

import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.ProductType;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.QSalesTarget;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SalesTargetQueryQueryRepositoryImpl implements SalesTargetQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public int findTargetAmount(Long shopId, Long staffId, ProductType type, YearMonth yearMonth) {

    QSalesTarget q = QSalesTarget.salesTarget;

    // Default
    BooleanExpression baseCondition =
        q.shopId
            .eq(shopId)
            .and(q.staffId.eq(staffId))
            .and(
                Expressions.numberTemplate(Integer.class, "YEAR({0})", q.targetYear)
                    .eq(yearMonth.getYear()))
            .and(q.targetMonth.eq(yearMonth.getMonthValue()));

    // type
    BooleanExpression typeCondition =
        switch (type) {
          case SERVICE, PRODUCT -> q.items.isTrue();
          case SESSION_PASS, PREPAID_PASS -> q.membership.isTrue();
        };

    Integer result =
        queryFactory.select(q.sales).from(q).where(baseCondition.and(typeCondition)).fetchOne();

    return result != null ? result : 0;
  }

  @Override
  public boolean existsTargetForShopInMonths(Long shopId, List<YearMonth> yearMonthList) {
    QSalesTarget q = QSalesTarget.salesTarget;

    BooleanExpression shopCondition = q.shopId.eq(shopId);

    BooleanBuilder dateCondition = new BooleanBuilder();
    for (YearMonth ym : yearMonthList) {
      dateCondition.or(
          Expressions.numberTemplate(Integer.class, "YEAR({0})", q.targetYear)
              .eq(ym.getYear())
              .and(q.targetMonth.eq(ym.getMonthValue())));
    }

    Integer fetch =
        queryFactory.selectOne().from(q).where(shopCondition.and(dateCondition)).fetchFirst();

    return fetch != null;
  }
}
