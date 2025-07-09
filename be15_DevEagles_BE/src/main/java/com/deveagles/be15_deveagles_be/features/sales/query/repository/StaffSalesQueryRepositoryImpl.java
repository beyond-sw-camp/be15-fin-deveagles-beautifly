package com.deveagles.be15_deveagles_be.features.sales.query.repository;

import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.Category;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.QPrimaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.QSecondaryItem;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.*;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.StaffNetSalesProjection;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.StaffNetSalesResponse;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.StaffPaymentsSalesResponse;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.StaffSalesDeductionsResponse;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StaffSalesQueryRepositoryImpl implements StaffSalesQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<StaffPaymentsSalesResponse> getSalesByStaff(
      Long staffId, LocalDateTime startDate, LocalDateTime endDate) {

    List<StaffPaymentsSalesResponse> result = new ArrayList<>();

    // 서비스/상품은 category 기준
    result.add(
        buildSalesResponse(
            "SERVICE", getSalesIdsByCategory(staffId, startDate, endDate, Category.SERVICE), true));
    result.add(
        buildSalesResponse(
            "PRODUCT", getSalesIdsByCategory(staffId, startDate, endDate, Category.PRODUCT), true));

    // 회수권
    result.add(
        buildSalesResponse(
            "SESSION_PASS", getSessionPassSalesIds(staffId, startDate, endDate), false));

    // 선불권
    result.add(
        buildSalesResponse(
            "PREPAID_PASS", getPrepaidPassSalesIds(staffId, startDate, endDate), false));

    return result;
  }

  private StaffPaymentsSalesResponse buildSalesResponse(
      String category, List<Long> salesIds, boolean couponApplicable) {

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

    List<StaffNetSalesResponse> netSalesList =
        projectionList.stream()
            .map(
                p ->
                    StaffNetSalesResponse.builder()
                        .paymentsMethod(PaymentsMethod.valueOf(p.getPayments()))
                        .amount(p.getAmount())
                        .incentiveAmount(0)
                        .build())
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
            sales.deletedAt.isNull())
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
            sales.deletedAt.isNull())
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
            sales.deletedAt.isNull())
        .distinct()
        .fetch();
  }
}
