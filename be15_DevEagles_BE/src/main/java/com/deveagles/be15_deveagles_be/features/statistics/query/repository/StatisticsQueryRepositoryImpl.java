package com.deveagles.be15_deveagles_be.features.statistics.query.repository;

import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.QCustomer;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.QPrimaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.QSecondaryItem;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.QItemSales;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.QSales;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.AdvancedSalesStatisticsResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.SalesStatisticsResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.SalesSummaryResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.StatisticsRequest;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StatisticsQueryRepositoryImpl implements StatisticsQueryRepository {

  private final JPAQueryFactory queryFactory;
  private final QSales sales = QSales.sales;
  private final QItemSales itemSales = QItemSales.itemSales;
  private final QPrimaryItem primaryItem = QPrimaryItem.primaryItem;
  private final QSecondaryItem secondaryItem = QSecondaryItem.secondaryItem;
  private final QCustomer customer = QCustomer.customer;

  @Override
  public List<SalesStatisticsResponse> findSalesStatisticsByPeriod(
      Long shopId, LocalDate startDate, LocalDate endDate) {
    return queryFactory
        .select(
            sales.salesDate.year(),
            sales.salesDate.month(),
            sales.salesDate.dayOfMonth(),
            sales.totalAmount.sum().coalesce(0),
            sales.count())
        .from(sales)
        .where(
            sales
                .shopId
                .eq(shopId)
                .and(
                    sales.salesDate.between(
                        startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                .and(sales.isRefunded.isFalse()))
        .groupBy(sales.salesDate.year(), sales.salesDate.month(), sales.salesDate.dayOfMonth())
        .orderBy(
            sales.salesDate.year().asc(),
            sales.salesDate.month().asc(),
            sales.salesDate.dayOfMonth().asc())
        .fetch()
        .stream()
        .map(
            tuple ->
                SalesStatisticsResponse.builder()
                    .date(
                        LocalDate.of(
                            tuple.get(0, Integer.class),
                            tuple.get(1, Integer.class),
                            tuple.get(2, Integer.class)))
                    .totalSalesAmount(tuple.get(3, Integer.class).longValue())
                    .totalTransactions(tuple.get(4, Long.class))
                    .build())
        .collect(Collectors.toList());
  }

  @Override
  public List<AdvancedSalesStatisticsResponse> findAdvancedSalesStatistics(
      Long shopId, StatisticsRequest request) {
    // 시간 기반 집계와 상품/카테고리 기반 집계를 완전히 분리
    StatisticsRequest.GroupBy groupBy =
        request.getGroupBy() != null ? request.getGroupBy() : StatisticsRequest.GroupBy.DAY;

    // 시간 기반 집계는 sales 테이블만 사용 (JOIN 없음)
    if (groupBy == StatisticsRequest.GroupBy.DAY
        || groupBy == StatisticsRequest.GroupBy.WEEK
        || groupBy == StatisticsRequest.GroupBy.MONTH) {

      return getTimeBasedStatistics(shopId, request, groupBy);
    }
    // 상품/카테고리 기반 집계만 JOIN 사용
    else {
      return getItemBasedStatistics(shopId, request, groupBy);
    }
  }

  /** 시간 기반 통계 - sales 테이블만 사용하여 정확한 집계 */
  private List<AdvancedSalesStatisticsResponse> getTimeBasedStatistics(
      Long shopId, StatisticsRequest request, StatisticsRequest.GroupBy groupBy) {
    // WHERE 조건 구성
    List<BooleanExpression> whereClauses = new ArrayList<>();
    whereClauses.add(sales.shopId.eq(shopId));
    whereClauses.add(sales.isRefunded.isFalse());
    handleTimeRange(request, whereClauses);

    // 그룹화 키 설정
    StringExpression groupKey;
    switch (groupBy) {
      case DAY:
        groupKey = sales.salesDate.dayOfMonth().stringValue();
        break;
      case WEEK:
        groupKey = sales.salesDate.week().stringValue();
        break;
      case MONTH:
        groupKey = sales.salesDate.month().stringValue();
        break;
      default:
        throw new IllegalArgumentException("Invalid time groupBy: " + groupBy);
    }

    // sales 테이블만 사용하여 정확한 집계 (기본 API와 동일한 방식)
    return queryFactory
        .select(
            groupKey,
            sales.totalAmount.sum().coalesce(0),
            sales.count().coalesce(0L),
            sales.discountAmount.sum().coalesce(0),
            // 쿠폰 할인액은 0으로 설정 (시간 기반에서는 정확한 계산 어려움)
            Expressions.constant(0))
        .from(sales)
        .where(whereClauses.toArray(new BooleanExpression[0]))
        .groupBy(groupKey)
        .orderBy(groupKey.asc())
        .fetch()
        .stream()
        .map(
            tuple ->
                AdvancedSalesStatisticsResponse.builder()
                    .date(tuple.get(0, String.class))
                    .gender(null)
                    .category(null)
                    .primaryItemName(null)
                    .secondaryItemName(null)
                    .totalSalesAmount(tuple.get(1, Integer.class).longValue())
                    .totalTransactions(tuple.get(2, Long.class))
                    .totalDiscountAmount(tuple.get(3, Integer.class).longValue())
                    .totalCouponDiscountAmount(tuple.get(4, Integer.class).longValue())
                    .build())
        .collect(Collectors.toList());
  }

  /** 상품/카테고리 기반 통계 - JOIN 사용하여 상품별 실제 금액 계산 */
  private List<AdvancedSalesStatisticsResponse> getItemBasedStatistics(
      Long shopId, StatisticsRequest request, StatisticsRequest.GroupBy groupBy) {
    JPAQuery<?> query = queryFactory.from(sales);

    // JOIN 테이블들 (LEFT JOIN으로 변경하되 조건 강화)
    query.leftJoin(itemSales).on(sales.salesId.eq(itemSales.salesId));
    query.leftJoin(secondaryItem).on(itemSales.secondaryItemId.eq(secondaryItem.secondaryItemId));
    query.leftJoin(primaryItem).on(secondaryItem.primaryItemId.eq(primaryItem.primaryItemId));

    // customer는 선택적 JOIN (GENDER 그룹화일 때만 필요)
    if (groupBy == StatisticsRequest.GroupBy.GENDER) {
      query.leftJoin(customer).on(sales.customerId.eq(customer.id));
    }

    // WHERE 조건
    List<BooleanExpression> whereClauses = new ArrayList<>();
    whereClauses.add(sales.shopId.eq(shopId));
    whereClauses.add(sales.isRefunded.isFalse());

    // 상품 기반 집계이므로 itemSales가 존재하는 경우만 포함
    whereClauses.add(itemSales.salesId.isNotNull());
    whereClauses.add(secondaryItem.secondaryItemId.isNotNull());
    whereClauses.add(primaryItem.primaryItemId.isNotNull());

    handleTimeRange(request, whereClauses);

    // 추가 필터 조건
    if (request.getGender() != null && groupBy == StatisticsRequest.GroupBy.GENDER) {
      whereClauses.add(
          customer.gender.isNotNull().and(customer.gender.stringValue().eq(request.getGender())));
    }
    if (request.getCategoryId() != null) {
      whereClauses.add(primaryItem.category.stringValue().eq(request.getCategoryId().toString()));
    }
    if (request.getPrimaryItemId() != null) {
      whereClauses.add(primaryItem.primaryItemId.eq(request.getPrimaryItemId()));
    }
    if (request.getSecondaryItemId() != null) {
      whereClauses.add(secondaryItem.secondaryItemId.eq(request.getSecondaryItemId()));
    }

    query.where(whereClauses.toArray(new BooleanExpression[0]));

    // 그룹화 키 설정
    StringExpression groupKey;
    switch (groupBy) {
      case GENDER:
        groupKey =
            new CaseBuilder()
                .when(customer.gender.isNotNull())
                .then(customer.gender.stringValue())
                .otherwise("UNKNOWN");
        break;
      case CATEGORY:
        groupKey = primaryItem.category.stringValue().coalesce("UNKNOWN");
        break;
      case PRIMARY_ITEM:
        groupKey = primaryItem.primaryItemName.coalesce("UNKNOWN");
        break;
      case SECONDARY_ITEM:
        groupKey = secondaryItem.secondaryItemName.coalesce("UNKNOWN");
        break;
      default:
        throw new IllegalArgumentException("Unsupported groupBy: " + groupBy);
    }

    // 실제 상품 금액으로 집계
    return query
        .groupBy(groupKey)
        .select(
            groupKey,
            // 실제 상품 금액 = quantity * secondaryItemPrice
            itemSales.quantity.multiply(secondaryItem.secondaryItemPrice).sum().coalesce(0),
            sales.salesId.countDistinct().coalesce(0L),
            // 할인액 비례 계산
            Expressions.numberTemplate(
                Integer.class,
                "COALESCE(SUM(CASE WHEN {0} > 0 THEN "
                    + "CAST(({1} * {2} * {3}) / {0} AS INTEGER) ELSE 0 END), 0)",
                sales.totalAmount,
                itemSales.quantity,
                secondaryItem.secondaryItemPrice,
                sales.discountAmount),
            // 쿠폰 할인액 비례 계산
            Expressions.numberTemplate(
                Integer.class,
                "COALESCE(SUM(CASE WHEN {0} > 0 AND {4} IS NOT NULL THEN "
                    + "CAST(({1} * {2} * {3}) / {0} AS INTEGER) ELSE 0 END), 0)",
                sales.totalAmount,
                itemSales.quantity,
                secondaryItem.secondaryItemPrice,
                sales.discountAmount,
                itemSales.couponId))
        .fetch()
        .stream()
        .map(
            tuple ->
                AdvancedSalesStatisticsResponse.builder()
                    .date(null)
                    .gender(
                        groupBy == StatisticsRequest.GroupBy.GENDER
                            ? tuple.get(0, String.class)
                            : null)
                    .category(
                        groupBy == StatisticsRequest.GroupBy.CATEGORY
                            ? tuple.get(0, String.class)
                            : null)
                    .primaryItemName(
                        groupBy == StatisticsRequest.GroupBy.PRIMARY_ITEM
                            ? tuple.get(0, String.class)
                            : null)
                    .secondaryItemName(
                        groupBy == StatisticsRequest.GroupBy.SECONDARY_ITEM
                            ? tuple.get(0, String.class)
                            : null)
                    .totalSalesAmount(tuple.get(1, Integer.class).longValue())
                    .totalTransactions(tuple.get(2, Long.class))
                    .totalDiscountAmount(tuple.get(3, Integer.class).longValue())
                    .totalCouponDiscountAmount(tuple.get(4, Integer.class).longValue())
                    .build())
        .collect(Collectors.toList());
  }

  private void handleTimeRange(StatisticsRequest request, List<BooleanExpression> whereClauses) {
    LocalDate now = LocalDate.now();
    if (request.getTimeRange() != null) {
      switch (request.getTimeRange()) {
        case LAST_WEEK:
          whereClauses.add(
              sales.salesDate.between(now.minusWeeks(1).atStartOfDay(), now.atTime(LocalTime.MAX)));
          break;
        case LAST_MONTH:
          whereClauses.add(
              sales.salesDate.between(
                  now.minusMonths(1).atStartOfDay(), now.atTime(LocalTime.MAX)));
          break;
        case LAST_6_MONTHS:
          whereClauses.add(
              sales.salesDate.between(
                  now.minusMonths(6).atStartOfDay(), now.atTime(LocalTime.MAX)));
          break;
        case LAST_YEAR:
          whereClauses.add(
              sales.salesDate.between(now.minusYears(1).atStartOfDay(), now.atTime(LocalTime.MAX)));
          break;
        case CUSTOM:
          if (request.getStartDate() != null && request.getEndDate() != null) {
            whereClauses.add(
                sales.salesDate.between(
                    request.getStartDate().atStartOfDay(),
                    request.getEndDate().atTime(LocalTime.MAX)));
          }
          break;
      }
    }
  }

  private List<AdvancedSalesStatisticsResponse> executeQueryWithGroupBy(
      JPAQuery<?> query, StatisticsRequest.GroupBy groupBy) {
    // 이 메서드는 더 이상 사용하지 않음 - 위의 분리된 메서드들로 대체됨
    throw new UnsupportedOperationException(
        "This method has been replaced by getTimeBasedStatistics and getItemBasedStatistics");
  }

  @Override
  public SalesSummaryResponse findSalesSummary(
      Long shopId, LocalDate startDate, LocalDate endDate) {
    // Get total sales and transaction count for the period
    var basicStats =
        queryFactory
            .select(sales.totalAmount.sum().coalesce(0), sales.count())
            .from(sales)
            .where(
                sales
                    .shopId
                    .eq(shopId)
                    .and(
                        sales.salesDate.between(
                            startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                    .and(sales.isRefunded.isFalse()))
            .fetchOne();

    Long totalSales = basicStats.get(0, Integer.class).longValue();
    Long totalTransactions = basicStats.get(1, Long.class);

    // Calculate daily average
    long daysBetween = startDate.until(endDate).getDays() + 1;
    double dailyAverage = daysBetween > 0 ? (double) totalSales / daysBetween : 0.0;

    // Calculate average order value
    double averageOrderValue =
        totalTransactions > 0 ? (double) totalSales / totalTransactions : 0.0;

    return SalesSummaryResponse.builder()
        .totalSales(totalSales)
        .dailyAverage(dailyAverage)
        .totalTransactions(totalTransactions)
        .averageOrderValue(averageOrderValue)
        .startDate(startDate)
        .endDate(endDate)
        .build();
  }
}
