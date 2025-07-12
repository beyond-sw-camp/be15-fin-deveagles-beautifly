package com.deveagles.be15_deveagles_be.features.staffsales.query.repository;

import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.Category;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.QPrimaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.QSecondaryItem;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.*;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.ProductType;
import com.deveagles.be15_deveagles_be.features.staffsales.query.dto.response.*;
import com.deveagles.be15_deveagles_be.features.staffsales.query.service.support.SalesCalculator;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StaffSalesQueryRepositoryImpl implements StaffSalesQueryRepository {

  private final JPAQueryFactory queryFactory;
  private final SalesCalculator salesCalculator;

  @Override
  public List<StaffPaymentsSalesResponse> getSalesByStaff(
      boolean isDetail, Long shopId, Long staffId, LocalDateTime startDate, LocalDateTime endDate) {

    List<StaffPaymentsSalesResponse> result = new ArrayList<>();

    // SERVICE
    result.add(
        buildSalesResponse(
            "SERVICE",
            getSalesIdsByCategory(staffId, startDate, endDate, Category.SERVICE),
            true,
            shopId,
            staffId,
            ProductType.SERVICE));

    // PRODUCT
    result.add(
        buildSalesResponse(
            "PRODUCT",
            getSalesIdsByCategory(staffId, startDate, endDate, Category.PRODUCT),
            true,
            shopId,
            staffId,
            ProductType.PRODUCT));

    // SESSION_PASS
    result.add(
        buildSalesResponse(
            "SESSION_PASS",
            getSessionPassSalesIds(staffId, startDate, endDate),
            false,
            shopId,
            staffId,
            ProductType.SESSION_PASS));

    // PREPAID_PASS
    result.add(
        buildSalesResponse(
            "PREPAID_PASS",
            getPrepaidPassSalesIds(staffId, startDate, endDate),
            false,
            shopId,
            staffId,
            ProductType.PREPAID_PASS));

    return result;
  }

  private StaffPaymentsSalesResponse buildSalesResponse(
      String category,
      List<Long> salesIds,
      boolean couponApplicable,
      Long shopId,
      Long staffId,
      ProductType productType) {

    QSales sales = QSales.sales;
    QItemSales itemSales = QItemSales.itemSales;
    QPayments payments = QPayments.payments;

    // 결제 수단별 실매출 조회
    List<StaffNetSalesProjection> projectionList =
        queryFactory
            .select(
                Projections.constructor(
                    StaffNetSalesProjection.class,
                    payments.paymentsMethod.stringValue(),
                    payments.amount.sum()))
            .from(payments)
            .where(
                payments.salesId.in(salesIds),
                payments.paymentsMethod.notIn(
                    PaymentsMethod.PREPAID_PASS, PaymentsMethod.SESSION_PASS),
                payments.deletedAt.isNull())
            .groupBy(payments.paymentsMethod)
            .fetch();

    Map<PaymentsMethod, Integer> rateMap =
        salesCalculator.getEffectiveIncentiveRates(shopId, staffId, productType);

    List<StaffNetSalesResponse> netSalesList =
        projectionList.stream()
            .map(
                p -> {
                  PaymentsMethod method = PaymentsMethod.valueOf(p.getPayments());
                  int amount = p.getAmount();
                  int rate = rateMap.getOrDefault(method, 0);
                  int incentiveAmount = (int) Math.floor(amount * rate / 100.0);

                  return StaffNetSalesResponse.builder()
                      .paymentsMethod(method)
                      .amount(amount)
                      .incentiveAmount(incentiveAmount)
                      .build();
                })
            .toList();

    // 할인 공제
    Integer discountSum =
        queryFactory
            .select(sales.discountAmount.sum())
            .from(sales)
            .where(sales.salesId.in(salesIds), sales.discountAmount.isNotNull())
            .fetchOne();

    // 쿠폰 공제
    Integer couponSum = 0;
    if (couponApplicable) {
      couponSum =
          Optional.ofNullable(
                  queryFactory
                      .select(itemSales.count())
                      .from(itemSales)
                      .where(itemSales.salesId.in(salesIds), itemSales.couponId.isNotNull())
                      .fetchOne())
              .map(Long::intValue)
              .orElse(0);
    }

    // 선불권 공제
    Integer prepaidSum =
        queryFactory
            .select(payments.amount.sum())
            .from(payments)
            .where(
                payments.salesId.in(salesIds),
                payments.paymentsMethod.eq(PaymentsMethod.PREPAID_PASS))
            .fetchOne();

    // 공제 항목 리스트
    List<StaffSalesDeductionsResponse> deductionList =
        List.of(
            StaffSalesDeductionsResponse.builder()
                .deduction("DISCOUNT")
                .amount(Optional.ofNullable(discountSum).orElse(0))
                .build(),
            StaffSalesDeductionsResponse.builder().deduction("COUPON").amount(couponSum).build(),
            StaffSalesDeductionsResponse.builder()
                .deduction("PREPAID")
                .amount(Optional.ofNullable(prepaidSum).orElse(0))
                .build());

    return StaffPaymentsSalesResponse.builder()
        .category(category)
        .netSalesList(netSalesList)
        .deductionList(deductionList)
        .build();
  }

  private List<Long> getSalesIdsByCategory(
      Long staffId, LocalDateTime start, LocalDateTime end, Category category) {
    QSales sales = QSales.sales;
    QItemSales itemSales = QItemSales.itemSales;
    QSecondaryItem secondaryItem = QSecondaryItem.secondaryItem;
    QPrimaryItem primaryItem = QPrimaryItem.primaryItem;

    return queryFactory
        .select(sales.salesId)
        .from(sales)
        .join(itemSales)
        .on(itemSales.salesId.eq(sales.salesId))
        .join(secondaryItem)
        .on(itemSales.secondaryItemId.eq(secondaryItem.secondaryItemId))
        .join(primaryItem)
        .on(secondaryItem.primaryItemId.eq(primaryItem.primaryItemId))
        .where(
            sales.staffId.eq(staffId),
            sales.salesDate.between(start, end),
            primaryItem.category.eq(category),
            sales.deletedAt.isNull(),
            sales.isRefunded.eq(Boolean.FALSE))
        .distinct()
        .fetch();
  }

  private List<Long> getSessionPassSalesIds(Long staffId, LocalDateTime start, LocalDateTime end) {
    QSales sales = QSales.sales;
    QSessionPassSales sessionPassSales = QSessionPassSales.sessionPassSales;

    return queryFactory
        .select(sales.salesId)
        .from(sales)
        .join(sessionPassSales)
        .on(sessionPassSales.salesId.eq(sales.salesId))
        .where(
            sales.staffId.eq(staffId),
            sales.salesDate.between(start, end),
            sales.deletedAt.isNull(),
            sales.isRefunded.eq(Boolean.FALSE))
        .distinct()
        .fetch();
  }

  private List<Long> getPrepaidPassSalesIds(Long staffId, LocalDateTime start, LocalDateTime end) {
    QSales sales = QSales.sales;
    QPrepaidPassSales prepaidPassSales = QPrepaidPassSales.prepaidPassSales;

    return queryFactory
        .select(sales.salesId)
        .from(sales)
        .join(prepaidPassSales)
        .on(prepaidPassSales.salesId.eq(sales.salesId))
        .where(
            sales.staffId.eq(staffId),
            sales.salesDate.between(start, end),
            sales.deletedAt.isNull(),
            sales.isRefunded.eq(Boolean.FALSE))
        .distinct()
        .fetch();
  }

  @Override
  public List<StaffPaymentsDetailSalesResponse> getDetailSalesByStaff(
      Long staffId, Long shopId, LocalDateTime startDate, LocalDateTime endDate) {

    List<StaffPaymentsDetailSalesResponse> result = new ArrayList<>();

    result.addAll(getSalesDetailByCategory(staffId, shopId, startDate, endDate, Category.SERVICE));
    result.addAll(getSalesDetailByCategory(staffId, shopId, startDate, endDate, Category.PRODUCT));

    return result;
  }

  private List<StaffPaymentsDetailSalesResponse> getSalesDetailByCategory(
      Long staffId,
      Long shopId,
      LocalDateTime startDate,
      LocalDateTime endDate,
      Category category) {

    QSales sales = QSales.sales;
    QItemSales itemSales = QItemSales.itemSales;
    QPrimaryItem primary = QPrimaryItem.primaryItem;
    QSecondaryItem secondary = QSecondaryItem.secondaryItem;

    List<Tuple> salesGroup =
        queryFactory
            .select(
                primary.primaryItemId,
                primary.primaryItemName,
                secondary.secondaryItemId,
                secondary.secondaryItemName,
                itemSales.salesId)
            .from(itemSales)
            .join(secondary)
            .on(itemSales.secondaryItemId.eq(secondary.secondaryItemId))
            .join(secondary.primaryItem, primary)
            .join(sales)
            .on(itemSales.salesId.eq(sales.salesId))
            .where(
                itemSales.deletedAt.isNull(),
                secondary.deletedAt.isNull(),
                primary.deletedAt.isNull(),
                sales.staffId.eq(staffId),
                sales.shopId.eq(shopId),
                sales.salesDate.between(startDate, endDate),
                sales.isRefunded.eq(Boolean.FALSE),
                sales.deletedAt.isNull(),
                primary.category.eq(category))
            .fetch();

    Map<Long, Map<String, List<Long>>> grouped = new LinkedHashMap<>();

    Map<Long, String> primaryNameMap = new HashMap<>();
    for (Tuple tuple : salesGroup) {
      Long primaryId = tuple.get(primary.primaryItemId);
      String primaryName = tuple.get(primary.primaryItemName);
      String secondaryName = tuple.get(secondary.secondaryItemName);
      Long salesId = tuple.get(itemSales.salesId);

      primaryNameMap.putIfAbsent(primaryId, primaryName);

      grouped
          .computeIfAbsent(primaryId, k -> new LinkedHashMap<>())
          .computeIfAbsent(secondaryName, k -> new ArrayList<>())
          .add(salesId);
    }

    List<StaffPaymentsDetailSalesResponse> result = new ArrayList<>();

    List<StaffPrimarySalesResponse> primaryList =
        grouped.entrySet().stream()
            .map(
                primaryEntry -> {
                  Long primaryId = primaryEntry.getKey();
                  String primaryName = primaryNameMap.get(primaryId);
                  List<StaffSecondarySalesResponse> secondaryList =
                      primaryEntry.getValue().entrySet().stream()
                          .map(
                              secondaryEntry -> {
                                List<Long> salesIds = secondaryEntry.getValue();

                                StaffPaymentsSalesResponse saleData =
                                    buildSalesResponse(
                                        category.name(),
                                        salesIds,
                                        true,
                                        shopId,
                                        staffId,
                                        ProductType.valueOf(category.name()));

                                return StaffSecondarySalesResponse.builder()
                                    .secondaryItemName(secondaryEntry.getKey())
                                    .netSalesList(saleData.getNetSalesList())
                                    .deductionList(saleData.getDeductionList())
                                    .incentiveTotal(
                                        saleData.getNetSalesList().stream()
                                            .mapToInt(StaffNetSalesResponse::getIncentiveAmount)
                                            .sum())
                                    .build();
                              })
                          .toList();

                  return StaffPrimarySalesResponse.builder()
                      .primaryItemId(primaryEntry.getKey())
                      .primaryItemName(primaryName)
                      .secondaryList(secondaryList)
                      .build();
                })
            .toList();

    result.add(
        StaffPaymentsDetailSalesResponse.builder()
            .category(category.name())
            .primaryList(primaryList)
            .build());

    return result;
  }
}
