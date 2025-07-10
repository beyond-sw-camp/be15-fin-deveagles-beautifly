package com.deveagles.be15_deveagles_be.features.sales.query.service.support;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.SearchMode;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.*;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Incentive;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.ProductType;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.IncentiveRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SalesCalculator {

  private final IncentiveRepository incentiveRepository;

  public StaffSalesSummaryResponse calculateSummary(
      Long shopId, List<StaffSalesListResponse> staffSalesList) {

    int totalNetSales = 0;
    int totalDiscount = 0;
    int totalCoupon = 0;
    int totalPrepaid = 0;
    int totalIncentiveAmount = 0;

    for (StaffSalesListResponse staff : staffSalesList) {
      Long staffId = staff.getStaffId();

      for (StaffPaymentsSalesResponse payment : staff.getPaymentsSalesList()) {
        ProductType type = ProductType.valueOf(payment.getCategory());

        // 1. 실매출 합산
        totalNetSales +=
            payment.getNetSalesList().stream()
                .mapToInt(n -> n.getAmount() != null ? n.getAmount() : 0)
                .sum();

        // 2. 공제 합산
        for (StaffSalesDeductionsResponse deduction : payment.getDeductionList()) {
          switch (deduction.getDeduction()) {
            case "DISCOUNT" -> totalDiscount += deduction.getAmount();
            case "COUPON" -> totalCoupon += deduction.getAmount();
            case "PREPAID" -> totalPrepaid += deduction.getAmount();
          }
        }

        // 3. 인센티브 계산
        int incentive = calculateTotalIncentive(shopId, staffId, type, payment.getNetSalesList());
        totalIncentiveAmount += incentive;
      }
    }

    return StaffSalesSummaryResponse.builder()
        .totalNetSales(totalNetSales)
        .totalDiscount(totalDiscount)
        .totalCoupon(totalCoupon)
        .totalPrepaid(totalPrepaid)
        .totalIncentiveAmount(totalIncentiveAmount)
        .build();
  }

  public StaffSalesSummaryResponse calculateFromDetailList(
      List<StaffPaymentsDetailSalesResponse> detailSales) {

    int totalNetSales = 0;
    int totalDiscount = 0;
    int totalCoupon = 0;
    int totalPrepaid = 0;
    int totalIncentiveAmount = 0;

    for (StaffPaymentsDetailSalesResponse detail : detailSales) {
      ProductType type = ProductType.valueOf(detail.getCategory());

      for (StaffPrimarySalesResponse primary : detail.getPrimaryList()) {
        for (StaffSecondarySalesResponse secondary : primary.getSecondaryList()) {
          // 1. 실매출
          totalNetSales +=
              secondary.getNetSalesList().stream()
                  .mapToInt(n -> n.getAmount() != null ? n.getAmount() : 0)
                  .sum();

          // 2. 공제
          for (StaffSalesDeductionsResponse deduction : secondary.getDeductionList()) {
            switch (deduction.getDeduction()) {
              case "DISCOUNT" -> totalDiscount += deduction.getAmount();
              case "COUPON" -> totalCoupon += deduction.getAmount();
              case "PREPAID" -> totalPrepaid += deduction.getAmount();
            }
          }

          // 3. 인센티브
          totalIncentiveAmount += secondary.getIncentiveTotal();
        }
      }
    }

    return StaffSalesSummaryResponse.builder()
        .totalNetSales(totalNetSales)
        .totalDiscount(totalDiscount)
        .totalCoupon(totalCoupon)
        .totalPrepaid(totalPrepaid)
        .totalIncentiveAmount(totalIncentiveAmount)
        .build();
  }

  public StaffSalesSummaryResponse calculateFromSummaryList(
      Long shopId, List<StaffDetailSalesListResponse> staffDetailList) {
    int totalNetSales = 0;
    int totalDiscount = 0;
    int totalCoupon = 0;
    int totalPrepaid = 0;
    int totalIncentiveAmount = 0;

    for (StaffDetailSalesListResponse staff : staffDetailList) {
      StaffSalesSummaryResponse summary = staff.getSummary();
      totalNetSales += summary.getTotalNetSales();
      totalDiscount += summary.getTotalDiscount();
      totalCoupon += summary.getTotalCoupon();
      totalPrepaid += summary.getTotalPrepaid();
      totalIncentiveAmount += summary.getTotalIncentiveAmount();
    }

    return StaffSalesSummaryResponse.builder()
        .totalNetSales(totalNetSales)
        .totalDiscount(totalDiscount)
        .totalCoupon(totalCoupon)
        .totalPrepaid(totalPrepaid)
        .totalIncentiveAmount(totalIncentiveAmount)
        .build();
  }

  // 인센티브 계산
  public int calculateTotalIncentive(
      Long shopId, Long staffId, ProductType type, List<StaffNetSalesResponse> netSalesList) {
    Map<PaymentsMethod, Integer> incentiveRateMap =
        getEffectiveIncentiveRates(shopId, staffId, type);

    return netSalesList.stream()
        .mapToInt(
            net -> {
              PaymentsMethod method = net.getPaymentsMethod();
              int amount = net.getAmount() != null ? net.getAmount() : 0;
              int rate = incentiveRateMap.getOrDefault(method, 0);
              return (int) Math.floor(amount * (rate / 100.0));
            })
        .sum();
  }

  public Map<PaymentsMethod, Integer> getEffectiveIncentiveRates(
      Long shopId, Long staffId, ProductType type) {
    List<Incentive> incentives =
        incentiveRepository.findActiveIncentivesByShopIdAndType(shopId, type, staffId);

    Map<PaymentsMethod, Integer> rateMap = new EnumMap<>(PaymentsMethod.class);

    // 1. 공통 인센티브 먼저 세팅
    incentives.stream()
        .filter(i -> i.getStaffId() == null)
        .forEach(i -> rateMap.put(i.getPaymentsMethod(), i.getIncentive()));

    // 2. 직원별 인센티브가 있으면 덮어쓰기
    incentives.stream()
        .filter(i -> staffId != null && staffId.equals(i.getStaffId()))
        .forEach(i -> rateMap.put(i.getPaymentsMethod(), i.getIncentive()));

    return rateMap;
  }

  public int calculateAdjustedTarget(
      SearchMode mode, int monthlyTarget, int monthDays, int periodDays) {
    if (mode == SearchMode.MONTH) {
      return monthlyTarget;
    }

    // 기간 기준 목표: (월목표 / 월일수) * 조회일수, 절삭
    return BigDecimal.valueOf(monthlyTarget)
        .divide(BigDecimal.valueOf(monthDays), 0, RoundingMode.DOWN)
        .multiply(BigDecimal.valueOf(periodDays))
        .intValue();
  }

  public double calculateAchievementRate(int actual, int target) {
    if (target == 0) return 0.0;
    return BigDecimal.valueOf(actual)
        .divide(BigDecimal.valueOf(target), 4, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100))
        .setScale(2, RoundingMode.HALF_UP)
        .doubleValue();
  }
}
