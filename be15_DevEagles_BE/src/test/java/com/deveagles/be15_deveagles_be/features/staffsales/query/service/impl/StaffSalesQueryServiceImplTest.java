package com.deveagles.be15_deveagles_be.features.staffsales.query.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.SearchMode;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.ProductType;
import com.deveagles.be15_deveagles_be.features.staffsales.query.dto.request.GetStaffSalesListRequest;
import com.deveagles.be15_deveagles_be.features.staffsales.query.dto.response.*;
import com.deveagles.be15_deveagles_be.features.staffsales.query.repository.SalesTargetQueryRepository;
import com.deveagles.be15_deveagles_be.features.staffsales.query.repository.StaffSalesQueryRepository;
import com.deveagles.be15_deveagles_be.features.staffsales.query.service.support.SalesCalculator;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("StaffSalesQueryService 단위 테스트")
public class StaffSalesQueryServiceImplTest {

  @InjectMocks private StaffSalesQueryServiceImpl staffSalesQueryService;

  @Mock private UserRepository userRepository;

  @Mock private StaffSalesQueryRepository staffSalesQueryRepository;

  @Mock private SalesTargetQueryRepository salesTargetQueryRepository;

  @Mock private SalesCalculator salesCalculator;

  @Test
  @DisplayName("직원 매출 요약 조회 - 성공")
  void getStaffSales_성공() {
    // given
    Long shopId = 1L;
    Long staffId = 101L;
    LocalDate start = LocalDate.of(2024, 6, 1);
    GetStaffSalesListRequest request = new GetStaffSalesListRequest(SearchMode.MONTH, start, null);

    Staff staff = Staff.builder().staffId(staffId).staffName("홍길동").build();

    StaffNetSalesResponse netSale =
        StaffNetSalesResponse.builder().paymentsMethod(PaymentsMethod.CARD).amount(10000).build();

    StaffSalesDeductionsResponse deduction =
        StaffSalesDeductionsResponse.builder().deduction("DISCOUNT").amount(2000).build();

    StaffPaymentsSalesResponse paymentsSales =
        StaffPaymentsSalesResponse.builder()
            .category("SERVICE")
            .netSalesList(List.of(netSale))
            .deductionList(List.of(deduction))
            .incentiveTotal(1000)
            .build();

    StaffSalesListResponse salesResponse =
        StaffSalesListResponse.builder()
            .staffId(staffId)
            .staffName("홍길동")
            .paymentsSalesList(List.of(paymentsSales))
            .build();

    StaffSalesSummaryResponse summary =
        StaffSalesSummaryResponse.builder()
            .totalNetSales(10000)
            .totalDiscount(2000)
            .totalCoupon(0)
            .totalPrepaid(0)
            .totalIncentiveAmount(1000)
            .build();

    // stub
    given(userRepository.findByShopIdAndLeftDateIsNull(shopId)).willReturn(List.of(staff));
    given(
            staffSalesQueryRepository.getSalesByStaff(
                false,
                shopId,
                staffId,
                start.withDayOfMonth(1).atStartOfDay(),
                start.withDayOfMonth(start.lengthOfMonth()).atTime(23, 59, 59)))
        .willReturn(List.of(paymentsSales));
    given(salesCalculator.getEffectiveIncentiveRates(shopId, staffId, ProductType.SERVICE))
        .willReturn(Map.of(PaymentsMethod.CARD, 10));
    given(salesCalculator.calculateSummary(eq(shopId), anyList())).willReturn(summary);

    // when
    StaffSalesListResult result = staffSalesQueryService.getStaffSales(shopId, request);

    // then
    assertThat(result.getStaffSalesList()).hasSize(1);
    assertThat(result.getStaffSalesList().get(0).getStaffName()).isEqualTo("홍길동");
    assertThat(result.getTotalSummary().getTotalNetSales()).isEqualTo(10000);
    assertThat(result.getTotalSummary().getTotalDiscount()).isEqualTo(2000);
    assertThat(result.getTotalSummary().getTotalIncentiveAmount()).isEqualTo(1000);
  }

  @DisplayName("getStaffDetailSales - 성공")
  @Test
  void getStaffDetailSales_성공() {
    // given
    Long shopId = 1L;
    Long staffId = 10L;
    String staffName = "홍길동";
    LocalDate start = LocalDate.of(2024, 6, 1);
    LocalDate end = LocalDate.of(2024, 6, 30);
    GetStaffSalesListRequest request = new GetStaffSalesListRequest(SearchMode.PERIOD, start, end);

    Staff staff = Staff.builder().staffId(staffId).staffName(staffName).build();

    List<Staff> staffList = List.of(staff);
    when(userRepository.findByShopIdAndLeftDateIsNull(shopId)).thenReturn(staffList);

    // 상세 매출
    StaffNetSalesResponse net =
        StaffNetSalesResponse.builder()
            .paymentsMethod(PaymentsMethod.CARD)
            .amount(20000)
            .incentiveAmount(2000)
            .build();

    StaffSalesDeductionsResponse deduction =
        StaffSalesDeductionsResponse.builder().deduction("DISCOUNT").amount(3000).build();

    StaffSecondarySalesResponse secondary =
        StaffSecondarySalesResponse.builder()
            .secondaryItemName("디자인 펌")
            .netSalesList(List.of(net))
            .deductionList(List.of(deduction))
            .incentiveTotal(2000)
            .build();

    StaffPrimarySalesResponse primary =
        StaffPrimarySalesResponse.builder()
            .primaryItemId(111L)
            .primaryItemName("시술")
            .secondaryList(List.of(secondary))
            .build();

    StaffPaymentsDetailSalesResponse detailSales =
        StaffPaymentsDetailSalesResponse.builder()
            .category("SERVICE")
            .primaryList(List.of(primary))
            .build();

    when(staffSalesQueryRepository.getDetailSalesByStaff(eq(staffId), eq(shopId), any(), any()))
        .thenReturn(List.of(detailSales));

    StaffSalesSummaryResponse summary =
        StaffSalesSummaryResponse.builder()
            .totalGrossSales(100000)
            .totalNetSales(90000)
            .totalDeduction(10000)
            .totalIncentiveAmount(3000)
            .build();

    given(salesCalculator.calculateFromDetailAndSalesList(any(), any())).willReturn(summary);

    when(salesCalculator.calculateFromSummaryList(eq(shopId), any())).thenReturn(summary);

    // 기본 매출 리스트
    StaffPaymentsSalesResponse sales =
        StaffPaymentsSalesResponse.builder()
            .category("SERVICE")
            .netSalesList(List.of(net))
            .deductionList(List.of(deduction))
            .incentiveTotal(2000)
            .build();
    when(staffSalesQueryRepository.getSalesByStaff(eq(true), eq(shopId), eq(staffId), any(), any()))
        .thenReturn(List.of(sales));

    // when
    StaffSalesDetailListResult result = staffSalesQueryService.getStaffDetailSales(shopId, request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getStaffSalesList()).hasSize(1);

    StaffDetailSalesListResponse response = result.getStaffSalesList().get(0);
    assertThat(response.getStaffId()).isEqualTo(staffId);
    assertThat(response.getStaffName()).isEqualTo(staffName);
    assertThat(response.getPaymentsSalesList()).hasSize(1);
    assertThat(response.getPaymentsDetailSalesList()).hasSize(1);
    assertThat(response.getSummary().getTotalNetSales()).isEqualTo(90000);
    assertThat(response.getSummary().getTotalIncentiveAmount()).isEqualTo(3000);
  }

  @DisplayName("getStaffSalesTarget - 성공")
  @Test
  void getStaffSalesTarget_성공() {
    // given
    Long shopId = 1L;
    GetStaffSalesListRequest request =
        new GetStaffSalesListRequest(SearchMode.MONTH, LocalDate.of(2024, 6, 1), null);

    LocalDate startDate = LocalDate.of(2024, 6, 1);
    LocalDate endDate = LocalDate.of(2024, 6, 30);

    given(salesTargetQueryRepository.existsTargetForShopInMonths(eq(shopId), anyList()))
        .willReturn(true);

    Staff staff = Staff.builder().staffId(10L).staffName("최승철").build();

    given(userRepository.findAllByShopId(shopId)).willReturn(List.of(staff));

    given(
            salesTargetQueryRepository.findTargetAmountByItemsOrMembership(
                eq(shopId), eq(10L), eq(true), any()))
        .willReturn(500000);
    given(
            salesTargetQueryRepository.findTargetAmountByItemsOrMembership(
                eq(shopId), eq(10L), eq(false), any()))
        .willReturn(200000);

    given(
            staffSalesQueryRepository.getTargetTotalSales(
                shopId, 10L, ProductType.SERVICE, startDate, endDate))
        .willReturn(300000);
    given(
            staffSalesQueryRepository.getTargetTotalSales(
                shopId, 10L, ProductType.PRODUCT, startDate, endDate))
        .willReturn(100000);
    given(
            staffSalesQueryRepository.getTargetTotalSales(
                shopId, 10L, ProductType.SESSION_PASS, startDate, endDate))
        .willReturn(50000);
    given(
            staffSalesQueryRepository.getTargetTotalSales(
                shopId, 10L, ProductType.PREPAID_PASS, startDate, endDate))
        .willReturn(10000);

    given(
            salesCalculator.calculateAdjustedTarget(
                eq(SearchMode.MONTH), anyInt(), anyInt(), anyInt()))
        .willAnswer(invocation -> invocation.getArgument(1)); // monthlyTarget 그대로 반환

    given(salesCalculator.calculateAchievementRate(anyInt(), anyInt())).willReturn(80.0);

    // when
    StaffSalesTargetListResult result = staffSalesQueryService.getStaffSalesTarget(shopId, request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getStaffSalesList()).hasSize(1);
    StaffSalesTargetResponse response = result.getStaffSalesList().get(0);
    assertThat(response.getStaffId()).isEqualTo(10L);
    assertThat(response.getTargetSalesList()).hasSize(2);
    assertThat(response.getTotalTargetAmount()).isEqualTo(700000);
    assertThat(response.getTotalActualAmount()).isEqualTo(460000);
    assertThat(response.getTotalAchievementRate()).isEqualTo(80.0);
  }

  @Test
  void getStaffSalesTarget_목표없음() {
    // given
    Long shopId = 1L;
    LocalDate start = LocalDate.of(2024, 6, 1);
    LocalDate end = LocalDate.of(2024, 6, 30);
    GetStaffSalesListRequest request = new GetStaffSalesListRequest(SearchMode.PERIOD, start, end);

    // 목표 매출이 존재하지 않음
    given(salesTargetQueryRepository.existsTargetForShopInMonths(eq(shopId), anyList()))
        .willReturn(false);

    // when
    StaffSalesTargetListResult result = staffSalesQueryService.getStaffSalesTarget(shopId, request);

    // then
    assertThat(result).isNull();
  }

  @DisplayName("getStaffSalesTarget - 월간 SearchMode의 날짜 범위 계산 검증")
  @Test
  void getStaffSalesTarget_월간범위계산_정확성검증() {
    // given
    Long shopId = 1L;
    LocalDate anyDayInMonth = LocalDate.of(2024, 6, 15); // 중간 날짜

    GetStaffSalesListRequest request =
        new GetStaffSalesListRequest(SearchMode.MONTH, anyDayInMonth, null);

    given(salesTargetQueryRepository.existsTargetForShopInMonths(eq(shopId), anyList()))
        .willReturn(true);

    Staff staff = Staff.builder().staffId(7L).staffName("이도겸").build();
    given(userRepository.findAllByShopId(shopId)).willReturn(List.of(staff));

    // 월간 요청 → 자동으로 6월 1일 ~ 6월 30일로 계산됨
    YearMonth june = YearMonth.of(2024, 6);

    given(salesTargetQueryRepository.findTargetAmountByItemsOrMembership(shopId, 7L, true, june))
        .willReturn(300000);
    given(salesTargetQueryRepository.findTargetAmountByItemsOrMembership(shopId, 7L, false, june))
        .willReturn(200000);

    given(
            staffSalesQueryRepository.getTargetTotalSales(
                shopId,
                7L,
                ProductType.SERVICE,
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 6, 30)))
        .willReturn(250000);
    given(
            staffSalesQueryRepository.getTargetTotalSales(
                shopId,
                7L,
                ProductType.PRODUCT,
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 6, 30)))
        .willReturn(100000);
    given(
            staffSalesQueryRepository.getTargetTotalSales(
                shopId,
                7L,
                ProductType.SESSION_PASS,
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 6, 30)))
        .willReturn(50000);
    given(
            staffSalesQueryRepository.getTargetTotalSales(
                shopId,
                7L,
                ProductType.PREPAID_PASS,
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 6, 30)))
        .willReturn(10000);

    given(
            salesCalculator.calculateAdjustedTarget(
                eq(SearchMode.MONTH), anyInt(), anyInt(), anyInt()))
        .willAnswer(invocation -> invocation.getArgument(1)); // monthlyTarget 그대로

    given(salesCalculator.calculateAchievementRate(anyInt(), anyInt())).willReturn(75.0);

    // when
    StaffSalesTargetListResult result = staffSalesQueryService.getStaffSalesTarget(shopId, request);

    // then
    assertThat(result).isNotNull();
    StaffSalesTargetResponse response = result.getStaffSalesList().get(0);

    assertThat(response.getStaffName()).isEqualTo("이도겸");
    assertThat(response.getTotalTargetAmount()).isEqualTo(500000);
    assertThat(response.getTotalActualAmount()).isEqualTo(410000);
    assertThat(response.getTotalAchievementRate()).isEqualTo(75.0);
  }
}
