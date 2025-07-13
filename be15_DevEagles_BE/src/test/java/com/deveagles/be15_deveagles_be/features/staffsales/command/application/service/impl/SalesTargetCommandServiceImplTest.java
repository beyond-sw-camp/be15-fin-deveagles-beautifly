package com.deveagles.be15_deveagles_be.features.staffsales.command.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.StaffSalesTargetInfo;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.request.SetSalesTargetRequest;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.SalesTarget;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.StaffSalesSettingType;
import com.deveagles.be15_deveagles_be.features.staffsales.command.repository.SalesTargetRepository;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("SalesTargetCommandService 단위 테스트")
public class SalesTargetCommandServiceImplTest {

  @InjectMocks private SalesTargetCommandServiceImpl salesTargetCommandService;

  @Mock private SalesTargetRepository salesTargetRepository;

  @DisplayName("setSalesTarget - 일괄 설정(BULK) 저장 성공")
  @Test
  void setSalesTarget_BULK_성공() {
    // given
    Long shopId = 1L;
    YearMonth targetMonth = YearMonth.of(2024, 7);
    Map<String, Integer> bulkTargets =
        Map.of(
            "membership", 300000,
            "items", 500000);

    SetSalesTargetRequest request =
        new SetSalesTargetRequest(
            targetMonth.atDay(1), StaffSalesSettingType.BULK, bulkTargets, null);

    // 기존에 STAFF 설정이 있던 경우
    SalesTarget staffTarget =
        SalesTarget.builder()
            .staffId(123L)
            .applyStatus(true)
            .membership(true)
            .targetYear(2024)
            .targetMonth(7)
            .build();

    given(salesTargetRepository.findByShopIdAndYearMonth(shopId, 2024, 7))
        .willReturn(List.of(staffTarget));

    // when
    salesTargetCommandService.setSalesTarget(shopId, request);

    // then
    assertThat(staffTarget.isApplyStatus()).isFalse(); // 기존 STAFF 데이터 비활성화
    then(salesTargetRepository).should(times(2)).save(any(SalesTarget.class));
  }

  @DisplayName("setSalesTarget - 직원별 설정(STAFF) 저장 성공")
  @Test
  void setSalesTarget_STAFF_성공() {
    // given
    Long shopId = 1L;
    YearMonth targetMonth = YearMonth.of(2024, 7);

    Map<String, Integer> amounts = Map.of("membership", 400000);
    StaffSalesTargetInfo info =
        StaffSalesTargetInfo.builder().staffId(10L).targetAmounts(amounts).build();

    SetSalesTargetRequest request =
        new SetSalesTargetRequest(
            targetMonth.atDay(1), StaffSalesSettingType.STAFF, null, List.of(info));

    // 기존 대상 없음
    given(salesTargetRepository.findByShopIdAndYearMonth(shopId, 2024, 7))
        .willReturn(Collections.emptyList());

    // when
    salesTargetCommandService.setSalesTarget(shopId, request);

    // then
    then(salesTargetRepository).should(times(1)).save(any(SalesTarget.class));
  }
}
