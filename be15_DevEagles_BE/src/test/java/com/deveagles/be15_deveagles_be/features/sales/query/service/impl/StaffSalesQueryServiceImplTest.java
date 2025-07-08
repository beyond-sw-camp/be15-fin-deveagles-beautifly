package com.deveagles.be15_deveagles_be.features.sales.query.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.SearchMode;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.request.GetStaffSalesListRequest;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.StaffPaymentsSalesResponse;
import com.deveagles.be15_deveagles_be.features.sales.query.dto.response.StaffSalesListResponse;
import com.deveagles.be15_deveagles_be.features.sales.query.repository.StaffSalesQueryRepository;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.deveagles.be15_deveagles_be.features.users.command.repository.UserRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("StaffSalesQueryService 단위 테스트")
public class StaffSalesQueryServiceImplTest {

  @InjectMocks private StaffSalesQueryServiceImpl staffSalesQueryService;

  @Mock private UserRepository userRepository;

  @Mock private StaffSalesQueryRepository staffSalesQueryRepository;

  @Test
  void getStaffSales_성공() {
    // given
    Long shopId = 1L;
    LocalDate startDate = LocalDate.of(2024, 6, 1);
    LocalDate endDate = LocalDate.of(2024, 6, 30);

    GetStaffSalesListRequest request =
        new GetStaffSalesListRequest(SearchMode.PERIOD, startDate, endDate);

    Staff staff = Staff.builder().staffId(10L).staffName("김테스트").build();

    StaffPaymentsSalesResponse payments =
        StaffPaymentsSalesResponse.builder()
            .category("SERVICE")
            .netSalesList(List.of())
            .deductionList(List.of())
            .build();

    Mockito.when(userRepository.findByShopIdAndLeftDateIsNull(shopId)).thenReturn(List.of(staff));

    Mockito.when(staffSalesQueryRepository.getSalesByStaff(eq(10L), any(), any()))
        .thenReturn(List.of(payments));

    // when
    List<StaffSalesListResponse> result = staffSalesQueryService.getStaffSales(shopId, request);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getStaffName()).isEqualTo("김테스트");
    assertThat(result.get(0).getPaymentsSalesList()).hasSize(1);
    assertThat(result.get(0).getPaymentsSalesList().get(0).getCategory()).isEqualTo("SERVICE");
  }
}
