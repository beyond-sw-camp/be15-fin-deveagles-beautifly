package com.deveagles.be15_deveagles_be.features.staffsales.query.service.impl;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.SearchMode;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.ProductType;
import com.deveagles.be15_deveagles_be.features.staffsales.query.dto.request.GetStaffSalesListRequest;
import com.deveagles.be15_deveagles_be.features.staffsales.query.dto.response.*;
import com.deveagles.be15_deveagles_be.features.staffsales.query.repository.SalesTargetQueryRepository;
import com.deveagles.be15_deveagles_be.features.staffsales.query.repository.StaffSalesQueryRepository;
import com.deveagles.be15_deveagles_be.features.staffsales.query.service.StaffSalesQueryService;
import com.deveagles.be15_deveagles_be.features.staffsales.query.service.support.SalesCalculator;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffSalesQueryServiceImpl implements StaffSalesQueryService {

  private final UserRepository userRepository;
  private final SalesTargetQueryRepository salesTargetQueryRepository;
  private final StaffSalesQueryRepository staffSalesQueryRepository;
  private final SalesCalculator salesCalculator;

  @Override
  public StaffSalesListResult getStaffSales(Long shopId, GetStaffSalesListRequest request) {

    // 1. 조회 기간 계산
    LocalDateTime startDate = getStartDate(request);
    LocalDateTime endDate = getEndDate(request);

    // 2. 재직 중인 직원 리스트 조회
    List<Staff> staffList = userRepository.findByShopIdAndLeftDateIsNull(shopId);

    // 3. 직원별 매출 데이터 생성
    List<StaffSalesListResponse> result =
        staffList.stream()
            .map(
                staff -> {
                  List<StaffPaymentsSalesResponse> paymentsSalesList =
                      staffSalesQueryRepository
                          .getSalesByStaff(false, shopId, staff.getStaffId(), startDate, endDate)
                          .stream()
                          .map(
                              response -> {

                                // 인센티브율 맵 조회
                                ProductType type = ProductType.valueOf(response.getCategory());
                                Map<PaymentsMethod, Integer> incentiveRateMap =
                                    salesCalculator.getEffectiveIncentiveRates(
                                        shopId, staff.getStaffId(), type);

                                // 실매출 리스트 계산 (PREPAID 제외)
                                List<StaffNetSalesResponse> netSalesList =
                                    response.getNetSalesList().stream()
                                        .filter(
                                            net ->
                                                net.getPaymentsMethod()
                                                    != PaymentsMethod.PREPAID_PASS)
                                        .map(
                                            net -> {
                                              int amount =
                                                  Optional.ofNullable(net.getAmount()).orElse(0);
                                              int rate =
                                                  incentiveRateMap.getOrDefault(
                                                      net.getPaymentsMethod(), 0);
                                              int incentiveAmount =
                                                  (int) Math.floor(amount * (rate / 100.0));
                                              return StaffNetSalesResponse.builder()
                                                  .paymentsMethod(net.getPaymentsMethod())
                                                  .amount(amount)
                                                  .incentiveAmount(incentiveAmount)
                                                  .build();
                                            })
                                        .toList();

                                // 총 인센티브 합계
                                int incentiveTotal =
                                    netSalesList.stream()
                                        .mapToInt(
                                            net ->
                                                Optional.ofNullable(net.getIncentiveAmount())
                                                    .orElse(0))
                                        .sum();

                                // 총영업액 = 결제 금액 총합
                                int grossSalesTotal =
                                    netSalesList.stream()
                                        .mapToInt(
                                            net -> Optional.ofNullable(net.getAmount()).orElse(0))
                                        .sum();

                                // 공제액 계산
                                int deductionTotal =
                                    response.getDeductionList().stream()
                                        .mapToInt(d -> Optional.ofNullable(d.getAmount()).orElse(0))
                                        .sum();

                                // 실매출 = 총영업액 - 공제액
                                int netSalesTotal = grossSalesTotal - deductionTotal;

                                return StaffPaymentsSalesResponse.builder()
                                    .category(response.getCategory())
                                    .netSalesList(netSalesList)
                                    .deductionList(response.getDeductionList())
                                    .incentiveTotal(incentiveTotal)
                                    .grossSalesTotal(grossSalesTotal)
                                    .deductionTotal(deductionTotal)
                                    .netSalesTotal(netSalesTotal)
                                    .build();
                              })
                          .toList();

                  return StaffSalesListResponse.builder()
                      .staffId(staff.getStaffId())
                      .staffName(staff.getStaffName())
                      .paymentsSalesList(paymentsSalesList)
                      .build();
                })
            .toList();

    // 4. 전체 요약 계산
    StaffSalesSummaryResponse summary = salesCalculator.calculateSummary(shopId, result);

    return StaffSalesListResult.builder().staffSalesList(result).totalSummary(summary).build();
  }

  @Override
  public StaffSalesDetailListResult getStaffDetailSales(
      Long shopId, GetStaffSalesListRequest request) {

    // 1. 기간 계산
    LocalDateTime startDate = getStartDate(request);
    LocalDateTime endDate = getEndDate(request);

    // 2. 직원 리스트 조회
    List<Staff> staffList = userRepository.findByShopIdAndLeftDateIsNull(shopId);

    // 3. 직원별 매출 데이터 조회
    List<StaffDetailSalesListResponse> result =
        staffList.stream()
            .map(
                staff -> {
                  List<StaffPaymentsDetailSalesResponse> detailSales =
                      staffSalesQueryRepository.getDetailSalesByStaff(
                          staff.getStaffId(), shopId, startDate, endDate);

                  List<StaffPaymentsSalesResponse> sales =
                      staffSalesQueryRepository.getSalesByStaff(
                          true, shopId, staff.getStaffId(), startDate, endDate);

                  // 직원 요약 정보
                  StaffSalesSummaryResponse summary =
                      salesCalculator.calculateFromDetailAndSalesList(detailSales, sales);

                  return StaffDetailSalesListResponse.builder()
                      .staffId(staff.getStaffId())
                      .staffName(staff.getStaffName())
                      .paymentsSalesList(sales)
                      .paymentsDetailSalesList(detailSales)
                      .summary(summary)
                      .build();
                })
            .toList();

    StaffSalesSummaryResponse totalSummary =
        salesCalculator.calculateFromSummaryList(shopId, result);

    return StaffSalesDetailListResult.builder()
        .staffSalesList(result)
        .totalSummary(totalSummary)
        .build();
  }

  @Override
  public StaffSalesTargetListResult getStaffSalesTarget(
      Long shopId, GetStaffSalesListRequest request) {

    LocalDate startDate =
        request.searchMode() == SearchMode.MONTH
            ? request.startDate().withDayOfMonth(1)
            : request.startDate();

    LocalDate endDate =
        request.searchMode() == SearchMode.PERIOD
            ? request.endDate()
            : YearMonth.from(request.startDate()).atEndOfMonth();

    if (!hasAnyTargetForShop(shopId, startDate, endDate)) {
      return null;
    }

    List<Staff> staffList = userRepository.findAllByShopId(shopId);

    List<StaffSalesTargetResponse> staffResponse =
        staffList.stream()
            .map(
                staff -> {
                  Long staffId = staff.getStaffId();

                  List<StaffProductTargetSalesResponse> targetList =
                      List.of(
                          buildCombinedTargetResponse(
                              shopId, staffId, true, "상품", startDate, endDate, request),
                          buildCombinedTargetResponse(
                              shopId, staffId, false, "회원권", startDate, endDate, request));

                  int totalTarget =
                      targetList.stream()
                          .mapToInt(StaffProductTargetSalesResponse::getTargetAmount)
                          .sum();
                  int totalActual =
                      targetList.stream()
                          .mapToInt(StaffProductTargetSalesResponse::getTotalAmount)
                          .sum();
                  double totalRate =
                      salesCalculator.calculateAchievementRate(totalActual, totalTarget);

                  return StaffSalesTargetResponse.builder()
                      .staffId(staffId)
                      .staffName(staff.getStaffName())
                      .targetSalesList(targetList)
                      .totalTargetAmount(totalTarget)
                      .totalActualAmount(totalActual)
                      .totalAchievementRate(totalRate)
                      .build();
                })
            .toList();

    return StaffSalesTargetListResult.builder().staffSalesList(staffResponse).build();
  }

  private StaffProductTargetSalesResponse buildCombinedTargetResponse(
      Long shopId,
      Long staffId,
      boolean isItems,
      String label,
      LocalDate startDate,
      LocalDate endDate,
      GetStaffSalesListRequest request) {

    List<YearMonth> months = getYearMonthsBetween(startDate, endDate);
    int totalAdjustedTarget = 0;

    for (YearMonth ym : months) {
      LocalDate monthStart = ym.atDay(1);
      LocalDate monthEnd = ym.atEndOfMonth();

      LocalDate overlapStart = startDate.isAfter(monthStart) ? startDate : monthStart;
      LocalDate overlapEnd = endDate.isBefore(monthEnd) ? endDate : monthEnd;
      int includedDays = (int) ChronoUnit.DAYS.between(overlapStart, overlapEnd) + 1;

      int monthlyTarget =
          salesTargetQueryRepository.findTargetAmountByItemsOrMembership(
              shopId, staffId, isItems, ym);

      int adjustedTarget =
          salesCalculator.calculateAdjustedTarget(
              request.searchMode(), monthlyTarget, ym.lengthOfMonth(), includedDays);

      totalAdjustedTarget += adjustedTarget;
    }

    ProductType type1 = isItems ? ProductType.SERVICE : ProductType.SESSION_PASS;
    ProductType type2 = isItems ? ProductType.PRODUCT : ProductType.PREPAID_PASS;

    int actualSales1 =
        staffSalesQueryRepository.getTargetTotalSales(shopId, staffId, type1, startDate, endDate);
    int actualSales2 =
        staffSalesQueryRepository.getTargetTotalSales(shopId, staffId, type2, startDate, endDate);
    int totalActualSales = actualSales1 + actualSales2;

    double achievement =
        salesCalculator.calculateAchievementRate(totalActualSales, totalAdjustedTarget);

    return StaffProductTargetSalesResponse.builder()
        .label(label)
        .targetAmount(totalAdjustedTarget)
        .totalAmount(totalActualSales)
        .achievementRate(achievement)
        .build();
  }

  private List<YearMonth> getYearMonthsBetween(LocalDate startDate, LocalDate endDate) {
    List<YearMonth> months = new ArrayList<>();
    YearMonth current = YearMonth.from(startDate);
    YearMonth last = YearMonth.from(endDate);

    while (!current.isAfter(last)) {
      months.add(current);
      current = current.plusMonths(1);
    }

    return months;
  }

  private LocalDateTime getStartDate(GetStaffSalesListRequest request) {
    if (request.searchMode() == SearchMode.MONTH) {
      return request.startDate().withDayOfMonth(1).atStartOfDay();
    }
    return request.startDate().atStartOfDay();
  }

  private LocalDateTime getEndDate(GetStaffSalesListRequest request) {
    LocalDate endDate =
        getEffectiveEndDate(request.startDate(), request.endDate(), request.searchMode());
    return endDate.atTime(23, 59, 59);
  }

  private LocalDate getEffectiveEndDate(LocalDate startDate, LocalDate endDate, SearchMode mode) {
    if (mode == SearchMode.MONTH) {
      return startDate.withDayOfMonth(startDate.lengthOfMonth());
    }
    return Optional.ofNullable(endDate).orElse(startDate);
  }

  private int getPeriodDays(LocalDate start, LocalDate end) {
    return (int) ChronoUnit.DAYS.between(start, end) + 1;
  }

  private boolean hasAnyTargetForShop(Long shopId, LocalDate startDate, LocalDate endDate) {
    List<YearMonth> yearMonthList = getYearMonthsInRange(startDate, endDate);
    return salesTargetQueryRepository.existsTargetForShopInMonths(shopId, yearMonthList);
  }

  private List<YearMonth> getYearMonthsInRange(LocalDate startDate, LocalDate endDate) {
    List<YearMonth> result = new ArrayList<>();
    YearMonth start = YearMonth.from(startDate);
    YearMonth end = YearMonth.from(endDate);

    while (!start.isAfter(end)) {
      result.add(start);
      start = start.plusMonths(1);
    }

    return result;
  }
}
