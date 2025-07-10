package com.deveagles.be15_deveagles_be.features.sales.query.service.impl;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.SearchMode;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.request.GetStaffSalesListRequest;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.*;
import com.deveagles.be15_deveagles_be.features.sales.query.repository.StaffSalesQueryRepository;
import com.deveagles.be15_deveagles_be.features.sales.query.service.StaffSalesQueryService;
import com.deveagles.be15_deveagles_be.features.sales.query.service.support.SalesCalculator;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.ProductType;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffSalesQueryServiceImpl implements StaffSalesQueryService {

  private final UserRepository userRepository;
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
                          .getSalesByStaff(false, staff.getStaffId(), startDate, endDate)
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
                          true, staff.getStaffId(), startDate, endDate);

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

  private LocalDateTime getStartDate(GetStaffSalesListRequest request) {
    if (request.searchMode() == SearchMode.MONTH) {
      return request.startDate().withDayOfMonth(1).atStartOfDay();
    }
    return request.startDate().atStartOfDay();
  }

  private LocalDateTime getEndDate(GetStaffSalesListRequest request) {
    if (request.searchMode() == SearchMode.MONTH) {
      return request
          .startDate()
          .withDayOfMonth(request.startDate().lengthOfMonth())
          .atTime(23, 59, 59);
    }
    return Optional.ofNullable(request.endDate())
        .map(d -> d.atTime(23, 59, 59))
        .orElse(request.startDate().atTime(23, 59, 59));
  }
}
