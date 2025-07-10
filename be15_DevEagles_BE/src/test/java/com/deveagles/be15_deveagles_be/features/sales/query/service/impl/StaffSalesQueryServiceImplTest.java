package com.deveagles.be15_deveagles_be.features.sales.query.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.SearchMode;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.ProductType;
import com.deveagles.be15_deveagles_be.features.staffsales.query.dto.request.GetStaffSalesListRequest;
import com.deveagles.be15_deveagles_be.features.staffsales.query.dto.response.*;
import com.deveagles.be15_deveagles_be.features.staffsales.query.repository.SalesQueryRepository;
import com.deveagles.be15_deveagles_be.features.staffsales.query.repository.SalesTargetQueryRepository;
import com.deveagles.be15_deveagles_be.features.staffsales.query.repository.StaffSalesQueryRepository;
import com.deveagles.be15_deveagles_be.features.staffsales.query.service.impl.StaffSalesQueryServiceImpl;
import com.deveagles.be15_deveagles_be.features.staffsales.query.service.support.SalesCalculator;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import java.time.LocalDate;
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

  @Mock private SalesQueryRepository salesQueryRepository;

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

    // 요약 계산 결과
    StaffSalesSummaryResponse summary =
        StaffSalesSummaryResponse.builder()
            .totalNetSales(20000)
            .totalDiscount(3000)
            .totalCoupon(0)
            .totalPrepaid(0)
            .totalIncentiveAmount(2000)
            .build();
    when(salesCalculator.calculateFromDetailList(any())).thenReturn(summary);
    when(salesCalculator.calculateFromSummaryList(eq(shopId), any())).thenReturn(summary);

    // 기본 매출 리스트
    StaffPaymentsSalesResponse sales =
        StaffPaymentsSalesResponse.builder()
            .category("SERVICE")
            .netSalesList(List.of(net))
            .deductionList(List.of(deduction))
            .incentiveTotal(2000)
            .build();
    when(staffSalesQueryRepository.getSalesByStaff(eq(true), eq(staffId), any(), any()))
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
    assertThat(response.getSummary().getTotalNetSales()).isEqualTo(20000);
    assertThat(response.getSummary().getTotalIncentiveAmount()).isEqualTo(2000);
  }

  @Test
  void getStaffSalesTarget_성공() {
    // given
    Long shopId = 1L;
    LocalDate start = LocalDate.of(2024, 6, 1);
    LocalDate end = LocalDate.of(2024, 6, 30);

    GetStaffSalesListRequest request = new GetStaffSalesListRequest(SearchMode.PERIOD, start, end);

    Staff staff = Staff.builder().staffId(100L).staffName("승철이").build();

    given(userRepository.findAllByShopId(shopId)).willReturn(List.of(staff));
    given(salesTargetQueryRepository.existsTargetForShopInMonths(eq(shopId), anyList()))
        .willReturn(true);

    // 상품 + 회원권에 대한 Target/Actual 매핑
    given(
            salesTargetQueryRepository.findTargetAmount(
                eq(shopId), eq(100L), eq(ProductType.SERVICE), any()))
        .willReturn(100000);
    given(
            salesTargetQueryRepository.findTargetAmount(
                eq(shopId), eq(100L), eq(ProductType.PRODUCT), any()))
        .willReturn(50000);
    given(
            salesTargetQueryRepository.findTargetAmount(
                eq(shopId), eq(100L), eq(ProductType.SESSION_PASS), any()))
        .willReturn(30000);
    given(
            salesTargetQueryRepository.findTargetAmount(
                eq(shopId), eq(100L), eq(ProductType.PREPAID_PASS), any()))
        .willReturn(20000);

    given(
            salesQueryRepository.findTotalSales(
                eq(shopId), eq(100L), eq(ProductType.SERVICE), eq(start), eq(end)))
        .willReturn(120000);
    given(
            salesQueryRepository.findTotalSales(
                eq(shopId), eq(100L), eq(ProductType.PRODUCT), eq(start), eq(end)))
        .willReturn(30000);
    given(
            salesQueryRepository.findTotalSales(
                eq(shopId), eq(100L), eq(ProductType.SESSION_PASS), eq(start), eq(end)))
        .willReturn(25000);
    given(
            salesQueryRepository.findTotalSales(
                eq(shopId), eq(100L), eq(ProductType.PREPAID_PASS), eq(start), eq(end)))
        .willReturn(10000);

    // 합산: 상품(150000), 회원권(50000)
    given(
            salesCalculator.calculateAdjustedTarget(
                eq(SearchMode.PERIOD), eq(150000), anyInt(), anyInt()))
        .willReturn(140000);
    given(
            salesCalculator.calculateAdjustedTarget(
                eq(SearchMode.PERIOD), eq(50000), anyInt(), anyInt()))
        .willReturn(50000);

    given(salesCalculator.calculateAchievementRate(150000, 140000)).willReturn(107.1);
    given(salesCalculator.calculateAchievementRate(35000, 50000)).willReturn(70.0);

    // when
    StaffSalesTargetListResult result = staffSalesQueryService.getStaffSalesTarget(shopId, request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getStaffSalesList()).hasSize(1);

    StaffSalesTargetResponse response = result.getStaffSalesList().get(0);
    assertThat(response.getStaffId()).isEqualTo(100L);
    assertThat(response.getStaffName()).isEqualTo("승철이");
    assertThat(response.getTargetSalesList()).hasSize(2);

    StaffProductTargetSalesResponse 상품 = response.getTargetSalesList().get(0);
    StaffProductTargetSalesResponse 회원권 = response.getTargetSalesList().get(1);

    assertThat(상품.getLabel()).isEqualTo("상품");
    assertThat(상품.getTargetAmount()).isEqualTo(140000);
    assertThat(상품.getTotalAmount()).isEqualTo(150000);
    assertThat(상품.getAchievementRate()).isEqualTo(107.1);

    assertThat(회원권.getLabel()).isEqualTo("회원권");
    assertThat(회원권.getTargetAmount()).isEqualTo(50000);
    assertThat(회원권.getTotalAmount()).isEqualTo(35000);
    assertThat(회원권.getAchievementRate()).isEqualTo(70.0);
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
}
