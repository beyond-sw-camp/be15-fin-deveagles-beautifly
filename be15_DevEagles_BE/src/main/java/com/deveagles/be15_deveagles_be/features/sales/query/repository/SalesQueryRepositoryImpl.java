package com.deveagles.be15_deveagles_be.features.sales.query.repository;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.QItemSales;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.QPrepaidPassSales;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.QSales;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.QSessionPassSales;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.ProductType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SalesQueryRepositoryImpl implements SalesQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public int findTotalSales(
      Long shopId, Long staffId, ProductType type, LocalDate startDate, LocalDate endDate) {
    QSales sales = QSales.sales;
    QItemSales itemSales = QItemSales.itemSales;
    QSessionPassSales sessionPassSales = QSessionPassSales.sessionPassSales;
    QPrepaidPassSales prepaidPassSales = QPrepaidPassSales.prepaidPassSales;

    BooleanExpression baseCondition =
        sales
            .shopId
            .eq(shopId)
            .and(sales.staffId.eq(staffId))
            .and(sales.salesDate.between(startDate.atStartOfDay(), endDate.atTime(23, 59, 59)))
            .and(sales.isRefunded.isFalse());

    // 상품 유형별 조인 분기
    return switch (type) {
      case SERVICE, PRODUCT ->
          queryFactory
              .select(sales.totalAmount.sum().coalesce(0))
              .from(sales)
              .join(itemSales)
              .on(itemSales.salesId.eq(sales.salesId))
              .where(baseCondition)
              .fetchOne();
      case SESSION_PASS ->
          queryFactory
              .select(sales.totalAmount.sum().coalesce(0))
              .from(sales)
              .join(sessionPassSales)
              .on(sessionPassSales.salesId.eq(sales.salesId))
              .where(baseCondition)
              .fetchOne();
      case PREPAID_PASS ->
          queryFactory
              .select(sales.totalAmount.sum().coalesce(0))
              .from(sales)
              .join(prepaidPassSales)
              .on(prepaidPassSales.salesId.eq(sales.salesId))
              .where(baseCondition)
              .fetchOne();
    };
  }
}
