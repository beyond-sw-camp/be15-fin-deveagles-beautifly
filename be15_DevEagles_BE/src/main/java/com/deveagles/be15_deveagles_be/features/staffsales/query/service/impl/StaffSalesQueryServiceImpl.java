package com.deveagles.be15_deveagles_be.features.staffsales.query.service.impl;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.SearchMode;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.ProductType;
import com.deveagles.be15_deveagles_be.features.staffsales.query.dto.request.GetStaffSalesListRequest;
import com.deveagles.be15_deveagles_be.features.staffsales.query.dto.response.*;
import com.deveagles.be15_deveagles_be.features.staffsales.query.repository.SalesQueryRepository;
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
  private final SalesQueryRepository salesQueryRepository;
  private final StaffSalesQueryRepository staffSalesQueryRepository;
  private final SalesCalculator salesCalculator;

  @Override
  public StaffSalesListResult getStaffSales(Long shopId, GetStaffSalesListRequest request) {

    // 1. 기간 계산
    LocalDateTime startDate = getStartDate(request);
    LocalDateTime endDate = getEndDate(request);

    // 2. 직원 리스트 조회
    List<Staff> staffList = userRepository.findByShopIdAndLeftDateIsNull(shopId);

    // 3. 직원별 매출 데이터 조회
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
                                // 매출 타입
                                ProductType type = ProductType.valueOf(response.getCategory());

                                // 인센티브 율
                                Map<PaymentsMethod, Integer> incentiveRateMap =
                                    salesCalculator.getEffectiveIncentiveRates(
                                        shopId, staff.getStaffId(), type);

                                // 단건 인센티브 포함
                                List<StaffNetSalesResponse> netSalesWithIncentive =
                                    response.getNetSalesList().stream()
                                        .map(
                                            net -> {
                                              int amount =
                                                  net.getAmount() != null ? net.getAmount() : 0;
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

                                // 총 인센티브 합산
                                int incentiveTotal =
                                    netSalesWithIncentive.stream()
                                        .mapToInt(
                                            n ->
                                                n.getIncentiveAmount() != null
                                                    ? n.getIncentiveAmount()
                                                    : 0)
                                        .sum();

                                return StaffPaymentsSalesResponse.builder()
                                    .category(response.getCategory())
                                    .netSalesList(netSalesWithIncentive)
                                    .deductionList(response.getDeductionList())
                                    .incentiveTotal(incentiveTotal)
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

    return StaffSalesListResult.builder()
        .staffSalesList(result)
        .totalSummary(salesCalculator.calculateSummary(shopId, result))
        .build();
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
                  // ProductType 별 상세 매출 조회
                  List<StaffPaymentsDetailSalesResponse> detailSales =
                      staffSalesQueryRepository.getDetailSalesByStaff(
                          staff.getStaffId(), shopId, startDate, endDate);

                  List<StaffPaymentsSalesResponse> sales =
                      staffSalesQueryRepository.getSalesByStaff(
                          true, shopId, staff.getStaffId(), startDate, endDate);

                  // 직원 요약 정보
                  StaffSalesSummaryResponse summary =
                      salesCalculator.calculateFromDetailList(detailSales);

                  return StaffDetailSalesListResponse.builder()
                      .staffId(staff.getStaffId())
                      .staffName(staff.getStaffName())
                      .paymentsSalesList(sales)
                      .paymentsDetailSalesList(detailSales)
                      .summary(summary)
                      .build();
                })
            .toList();

    return StaffSalesDetailListResult.builder()
        .staffSalesList(result)
        .totalSummary(salesCalculator.calculateFromSummaryList(shopId, result))
        .build();
  }

  @Override
  public StaffSalesTargetListResult getStaffSalesTarget(
      Long shopId, GetStaffSalesListRequest request) {

    boolean isPeriodMode = request.searchMode() == SearchMode.PERIOD;
    LocalDate startDate = request.startDate();
    LocalDate endDate = isPeriodMode ? request.endDate() : startDate;

    if (!hasAnyTargetForShop(shopId, startDate, endDate)) {
      throw new BusinessException(ErrorCode.SHOP_NOT_FOUNT);
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
                              shopId,
                              staff.getStaffId(),
                              ProductType.SERVICE,
                              ProductType.PRODUCT,
                              "상품",
                              startDate,
                              endDate,
                              request),
                          buildCombinedTargetResponse(
                              shopId,
                              staff.getStaffId(),
                              ProductType.SESSION_PASS,
                              ProductType.PREPAID_PASS,
                              "회원권",
                              startDate,
                              endDate,
                              request));

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
      ProductType type1,
      ProductType type2,
      String label,
      LocalDate startDate,
      LocalDate endDate,
      GetStaffSalesListRequest request) {

    LocalDate realEndDate = Optional.ofNullable(endDate).orElse(startDate);

    int monthlyTarget1 =
        salesTargetQueryRepository.findTargetAmount(
            shopId, staffId, type1, YearMonth.from(startDate));
    int monthlyTarget2 =
        salesTargetQueryRepository.findTargetAmount(
            shopId, staffId, type2, YearMonth.from(startDate));

    int totalAmount1 =
        salesQueryRepository.findTotalSales(shopId, staffId, type1, startDate, realEndDate);
    int totalAmount2 =
        salesQueryRepository.findTotalSales(shopId, staffId, type2, startDate, realEndDate);

    int monthlyTotal = monthlyTarget1 + monthlyTarget2;
    int totalSales = totalAmount1 + totalAmount2;

    int adjustedTarget =
        salesCalculator.calculateAdjustedTarget(
            request.searchMode(),
            monthlyTotal,
            YearMonth.from(startDate).lengthOfMonth(),
            getPeriodDays(startDate, Optional.ofNullable(request.endDate()).orElse(startDate)));

    double achievement = salesCalculator.calculateAchievementRate(totalSales, adjustedTarget);

    return StaffProductTargetSalesResponse.builder()
        .label(label)
        .targetAmount(adjustedTarget)
        .totalAmount(totalSales)
        .achievementRate(achievement)
        .build();
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
