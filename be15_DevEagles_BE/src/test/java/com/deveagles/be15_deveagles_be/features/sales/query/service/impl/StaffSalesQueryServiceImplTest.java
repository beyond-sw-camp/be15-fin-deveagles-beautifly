package com.deveagles.be15_deveagles_be.features.sales.query.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.PaymentsMethod;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.SearchMode;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.request.GetStaffSalesListRequest;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.*;
import com.deveagles.be15_deveagles_be.features.sales.query.repository.StaffSalesQueryRepository;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Incentive;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.IncentiveRepository;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
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

  @Mock private IncentiveRepository incentiveRepository;

  @Test
  void getStaffSales_성공() {
    // given
    Long shopId = 1L;
    Long staffId = 10L;
    LocalDate start = LocalDate.of(2024, 6, 1);
    GetStaffSalesListRequest request =
        new GetStaffSalesListRequest(SearchMode.MONTH, LocalDate.of(2024, 6, 1), null);

    Staff staff = Staff.builder().staffId(staffId).staffName("홍길동").build();

    StaffNetSalesResponse netSale =
        StaffNetSalesResponse.builder()
            .paymentsMethod(PaymentsMethod.CARD)
            .amount(10000)
            .incentiveAmount(1000)
            .build();

    StaffSalesDeductionsResponse deduction =
        StaffSalesDeductionsResponse.builder().deduction("DISCOUNT").amount(2000).build();

    StaffPaymentsSalesResponse paymentsSales =
        StaffPaymentsSalesResponse.builder()
            .category("SERVICE")
            .netSalesList(List.of(netSale))
            .deductionList(List.of(deduction))
            .build();

    given(userRepository.findByShopIdAndLeftDateIsNull(shopId)).willReturn(List.of(staff));
    given(staffSalesQueryRepository.getSalesByStaff(any(), any(), any()))
        .willReturn(
            List.of(
                StaffPaymentsSalesResponse.builder()
                    .category("SERVICE")
                    .netSalesList(
                        List.of(
                            StaffNetSalesResponse.builder()
                                .paymentsMethod(PaymentsMethod.CARD)
                                .amount(10000)
                                .incentiveAmount(1000)
                                .build()))
                    .deductionList(
                        List.of(
                            StaffSalesDeductionsResponse.builder()
                                .deduction("DISCOUNT")
                                .amount(2000)
                                .build()))
                    .incentiveTotal(1000) // 총합 인센티브
                    .build()));
    given(incentiveRepository.findActiveIncentivesByShopIdAndType(any(), any(), any()))
        .willReturn(
            List.of(
                Incentive.builder()
                    .staffId(staffId)
                    .paymentsMethod(PaymentsMethod.CARD)
                    .incentive(10)
                    .build()));

    // when
    StaffSalesListResult result = staffSalesQueryService.getStaffSales(shopId, request);

    // then
    assertThat(result.getStaffSalesList()).hasSize(1);
    assertThat(result.getTotalSummary().getTotalNetSales()).isEqualTo(10000);
    assertThat(result.getTotalSummary().getTotalDiscount()).isEqualTo(2000);
    assertThat(result.getTotalSummary().getTotalIncentiveAmount()).isEqualTo(1000); // 10% of 10000
  }
}
