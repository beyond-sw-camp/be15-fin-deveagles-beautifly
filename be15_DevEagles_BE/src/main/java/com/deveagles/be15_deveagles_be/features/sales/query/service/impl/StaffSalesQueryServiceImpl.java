package com.deveagles.be15_deveagles_be.features.sales.query.service.impl;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.SearchMode;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.request.GetStaffSalesListRequest;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.*;
import com.deveagles.be15_deveagles_be.features.sales.query.repository.StaffSalesQueryRepository;
import com.deveagles.be15_deveagles_be.features.sales.query.service.StaffSalesQueryService;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Incentive;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.ProductType;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.IncentiveRepository;
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
  private final IncentiveRepository incentiveRepository;

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
                          .getSalesByStaff(staff.getStaffId(), startDate, endDate)
                          .stream()
                          .map(
                              response -> {
                                // 매출 타입
                                ProductType type = ProductType.valueOf(response.getCategory());

                                // 인센티브 율
                                Map<PaymentsMethod, Integer> incentiveRateMap =
                                    getEffectiveIncentiveRates(shopId, staff.getStaffId(), type);

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
        .totalSummary(calculateSummary(shopId, result))
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

  private StaffSalesSummaryResponse calculateSummary(
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

  private Map<PaymentsMethod, Integer> getEffectiveIncentiveRates(
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

  // 인센티브 계산
  private int calculateTotalIncentive(
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
}
