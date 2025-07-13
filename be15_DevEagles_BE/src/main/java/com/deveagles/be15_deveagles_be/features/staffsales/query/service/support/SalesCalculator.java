package com.deveagles.be15_deveagles_be.features.staffsales.query.service.support;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.SearchMode;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.Incentive;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.ProductType;
import com.deveagles.be15_deveagles_be.features.staffsales.command.repository.IncentiveRepository;
import com.deveagles.be15_deveagles_be.features.staffsales.query.dto.response.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SalesCalculator {

  private final IncentiveRepository incentiveRepository;

  public StaffSalesSummaryResponse calculateSummary(
      Long shopId, List<StaffSalesListResponse> staffSalesList) {

    int totalGrossSales = 0;
    int totalNetSales = 0;
    int totalDeduction = 0;
    int totalDiscount = 0;
    int totalCoupon = 0;
    int totalPrepaid = 0;
    int totalIncentiveAmount = 0;

    for (StaffSalesListResponse staff : staffSalesList) {
      Long staffId = staff.getStaffId();

      for (StaffPaymentsSalesResponse payment : staff.getPaymentsSalesList()) {
        ProductType type = ProductType.valueOf(payment.getCategory());

        // 총영업액 / 실매출 / 공제 합산
        totalGrossSales += payment.getGrossSalesTotal();
        totalNetSales += payment.getNetSalesTotal();
        totalDeduction += payment.getDeductionTotal();

        // 공제 항목별 세부 합산
        for (StaffSalesDeductionsResponse deduction : payment.getDeductionList()) {
          switch (deduction.getDeduction()) {
            case "DISCOUNT" -> totalDiscount += deduction.getAmount();
            case "COUPON" -> totalCoupon += deduction.getAmount();
            case "PREPAID" -> totalPrepaid += deduction.getAmount();
          }
        }

        // 인센티브 합산
        int incentive = calculateTotalIncentive(shopId, staffId, type, payment.getNetSalesList());
        totalIncentiveAmount += incentive;
      }
    }

    return StaffSalesSummaryResponse.builder()
        .totalGrossSales(totalGrossSales)
        .totalNetSales(totalNetSales)
        .totalDeduction(totalDeduction)
        .totalDiscount(totalDiscount)
        .totalCoupon(totalCoupon)
        .totalPrepaid(totalPrepaid)
        .totalIncentiveAmount(totalIncentiveAmount)
        .build();
  }

  public StaffSalesSummaryResponse calculateFromSummaryList(
      Long shopId, List<StaffDetailSalesListResponse> staffDetailList) {
    int[] netSales = {0};
    int[] discount = {0};
    int[] coupon = {0};
    int[] prepaid = {0};
    int[] incentiveTotal = {0};

    staffDetailList.forEach(
        staff -> {
          StaffSalesSummaryResponse summary = staff.getSummary();

          netSales[0] += Optional.ofNullable(summary.getTotalNetSales()).orElse(0);
          discount[0] += Optional.ofNullable(summary.getTotalDiscount()).orElse(0);
          coupon[0] += Optional.ofNullable(summary.getTotalCoupon()).orElse(0);
          prepaid[0] += Optional.ofNullable(summary.getTotalPrepaid()).orElse(0);
          incentiveTotal[0] += Optional.ofNullable(summary.getTotalIncentiveAmount()).orElse(0);
        });

    return StaffSalesSummaryResponse.builder()
        .totalNetSales(netSales[0])
        .totalDiscount(discount[0])
        .totalCoupon(coupon[0])
        .totalPrepaid(prepaid[0])
        .totalIncentiveAmount(incentiveTotal[0])
        .build();
  }

  // 인센티브 계산
  public int calculateTotalIncentive(
      Long shopId, Long staffId, ProductType type, List<StaffNetSalesResponse> netSalesList) {

    Map<PaymentsMethod, Integer> rateMap = getEffectiveIncentiveRates(shopId, staffId, type);

    return netSalesList.stream()
        .mapToInt(
            net -> {
              PaymentsMethod method = net.getPaymentsMethod();
              int amount = Optional.ofNullable(net.getAmount()).orElse(0);
              int rate = rateMap.getOrDefault(method, 0);
              return calculateIncentive(amount, rate);
            })
        .sum();
  }

  public int calculateIncentive(int amount, int rate) {
    return BigDecimal.valueOf(amount)
        .multiply(BigDecimal.valueOf(rate))
        .divide(BigDecimal.valueOf(100), 0, RoundingMode.DOWN)
        .intValue();
  }

  public Map<PaymentsMethod, Integer> getEffectiveIncentiveRates(
      Long shopId, Long staffId, ProductType type) {

    List<Incentive> incentives =
        incentiveRepository.findActiveIncentivesByShopIdAndType(shopId, type, staffId);

    Map<PaymentsMethod, Integer> rateMap = new EnumMap<>(PaymentsMethod.class);

    incentives.stream()
        .filter(incentive -> incentive.getStaffId() == null)
        .forEach(incentive -> rateMap.put(incentive.getPaymentsMethod(), incentive.getIncentive()));

    if (staffId != null) {
      incentives.stream()
          .filter(incentive -> staffId.equals(incentive.getStaffId()))
          .forEach(
              incentive -> rateMap.put(incentive.getPaymentsMethod(), incentive.getIncentive()));
    }
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

  public StaffSalesSummaryResponse calculateFromDetailAndSalesList(
      List<StaffPaymentsDetailSalesResponse> detailSales, List<StaffPaymentsSalesResponse> sales) {

    int totalNetSales = 0;
    int totalDiscount = 0;
    int totalCoupon = 0;
    int totalPrepaid = 0;
    int totalIncentiveAmount = 0;

    // 상세 매출: SERVICE, PRODUCT
    for (StaffPaymentsDetailSalesResponse category : detailSales) {
      for (StaffPrimarySalesResponse primary : category.getPrimaryList()) {
        for (StaffSecondarySalesResponse secondary : primary.getSecondaryList()) {
          // 실매출 및 인센티브
          for (StaffNetSalesResponse net : secondary.getNetSalesList()) {
            if (net.getPaymentsMethod() != PaymentsMethod.PREPAID_PASS) {
              totalNetSales += Optional.ofNullable(net.getAmount()).orElse(0);
              totalIncentiveAmount += Optional.ofNullable(net.getIncentiveAmount()).orElse(0);
            }
          }

          // 공제 항목
          for (StaffSalesDeductionsResponse deduction : secondary.getDeductionList()) {
            switch (deduction.getDeduction()) {
              case "DISCOUNT" ->
                  totalDiscount += Optional.ofNullable(deduction.getAmount()).orElse(0);
              case "COUPON" -> totalCoupon += Optional.ofNullable(deduction.getAmount()).orElse(0);
              case "PREPAID" ->
                  totalPrepaid += Optional.ofNullable(deduction.getAmount()).orElse(0);
            }
          }
        }
      }
    }

    // 일반 매출: SESSION_PASS, PREPAID_PASS
    for (StaffPaymentsSalesResponse payment : sales) {
      totalNetSales += payment.getNetSalesTotal();
      totalDiscount += sumDeduction(payment.getDeductionList(), "DISCOUNT");
      totalCoupon += sumDeduction(payment.getDeductionList(), "COUPON");
      totalPrepaid += sumDeduction(payment.getDeductionList(), "PREPAID");
      totalIncentiveAmount += payment.getIncentiveTotal();
    }

    int totalDeduction = totalDiscount + totalCoupon + totalPrepaid;
    int totalGrossSales = totalNetSales + totalDeduction;

    return StaffSalesSummaryResponse.builder()
        .totalNetSales(totalNetSales)
        .totalDiscount(totalDiscount)
        .totalCoupon(totalCoupon)
        .totalPrepaid(totalPrepaid)
        .totalDeduction(totalDeduction)
        .totalGrossSales(totalGrossSales)
        .totalIncentiveAmount(totalIncentiveAmount)
        .build();
  }

  private int sumDeduction(List<StaffSalesDeductionsResponse> list, String type) {
    return list == null
        ? 0
        : list.stream()
            .filter(d -> type.equals(d.getDeduction()))
            .mapToInt(d -> Optional.ofNullable(d.getAmount()).orElse(0))
            .sum();
  }
}
