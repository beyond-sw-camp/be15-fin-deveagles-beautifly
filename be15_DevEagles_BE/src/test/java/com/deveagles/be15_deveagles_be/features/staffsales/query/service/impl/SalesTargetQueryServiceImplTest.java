package com.deveagles.be15_deveagles_be.features.staffsales.query.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.StaffSalesTargetInfo;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.request.GetSalesTargetRequest;
import com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response.SalesTargetListResult;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.SalesTarget;
import com.deveagles.be15_deveagles_be.features.staffsales.command.domain.aggregate.StaffSalesSettingType;
import com.deveagles.be15_deveagles_be.features.staffsales.query.repository.SalesTargetQueryRepository;
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
@DisplayName("SalesTargetQueryService 단위 테스트")
public class SalesTargetQueryServiceImplTest {

  @InjectMocks private SalesTargetQueryServiceImpl service;

  @Mock private UserRepository userRepository;

  @Mock private SalesTargetQueryRepository salesTargetQueryRepository;

  @DisplayName("getSalesTarget - 일괄 설정(BULK)만 존재하는 경우")
  @Test
  void getSalesTarget_BULK_성공() {
    // given
    Long shopId = 1L;
    LocalDate targetDate = LocalDate.of(2024, 6, 1);
    GetSalesTargetRequest request = new GetSalesTargetRequest(targetDate);

    Staff staff1 = Staff.builder().staffId(100L).staffName("스태프1").build();
    Staff staff2 = Staff.builder().staffId(101L).staffName("스태프2").build();

    List<Staff> staffList = List.of(staff1, staff2);
    given(userRepository.findByShopIdAndLeftDateIsNull(shopId)).willReturn(staffList);

    SalesTarget bulkItems =
        SalesTarget.builder()
            .staffId(null)
            .items(true)
            .sales(200000)
            .targetYear(2024)
            .targetMonth(6)
            .build();

    SalesTarget bulkMembership =
        SalesTarget.builder()
            .staffId(null)
            .membership(true)
            .sales(150000)
            .targetYear(2024)
            .targetMonth(6)
            .build();

    List<SalesTarget> targets = List.of(bulkItems, bulkMembership);
    given(salesTargetQueryRepository.findAppliedTargets(eq(shopId), any(), any()))
        .willReturn(targets);

    // when
    SalesTargetListResult result = service.getSalesTarget(shopId, request);

    // then
    assertThat(result.getType()).isEqualTo(StaffSalesSettingType.BULK);
    assertThat(result.getStaffList()).hasSize(2);
    assertThat(result.getSalesTargetInfos()).hasSize(2);

    for (StaffSalesTargetInfo info : result.getSalesTargetInfos()) {
      assertThat(info.getTargetAmounts().get("items")).isEqualTo(200000);
      assertThat(info.getTargetAmounts().get("membership")).isEqualTo(150000);
    }
  }

  @DisplayName("getSalesTarget - STAFF 설정이 포함된 경우")
  @Test
  void getSalesTarget_STAFF_성공() {
    // given
    Long shopId = 1L;
    LocalDate targetDate = LocalDate.of(2024, 6, 1);
    GetSalesTargetRequest request = new GetSalesTargetRequest(targetDate);

    Staff staff1 = Staff.builder().staffId(200L).staffName("스태프A").build();
    Staff staff2 = Staff.builder().staffId(201L).staffName("스태프B").build();

    given(userRepository.findByShopIdAndLeftDateIsNull(shopId)).willReturn(List.of(staff1, staff2));

    SalesTarget staff1Items =
        SalesTarget.builder()
            .staffId(200L)
            .items(true)
            .sales(111000)
            .targetYear(2024)
            .targetMonth(6)
            .build();

    SalesTarget staff2Membership =
        SalesTarget.builder()
            .staffId(201L)
            .membership(true)
            .sales(222000)
            .targetYear(2024)
            .targetMonth(6)
            .build();

    List<SalesTarget> targets = List.of(staff1Items, staff2Membership);
    given(salesTargetQueryRepository.findAppliedTargets(eq(shopId), any(), any()))
        .willReturn(targets);

    // when
    SalesTargetListResult result = service.getSalesTarget(shopId, request);

    // then
    assertThat(result.getType()).isEqualTo(StaffSalesSettingType.STAFF);
    assertThat(result.getSalesTargetInfos()).hasSize(2);

    StaffSalesTargetInfo info1 =
        result.getSalesTargetInfos().stream()
            .filter(i -> i.getStaffId().equals(200L))
            .findFirst()
            .orElseThrow();

    assertThat(info1.getTargetAmounts().get("items")).isEqualTo(111000);
    assertThat(info1.getTargetAmounts().get("membership")).isNull();

    StaffSalesTargetInfo info2 =
        result.getSalesTargetInfos().stream()
            .filter(i -> i.getStaffId().equals(201L))
            .findFirst()
            .orElseThrow();

    assertThat(info2.getTargetAmounts().get("membership")).isEqualTo(222000);
    assertThat(info2.getTargetAmounts().get("items")).isNull();
  }

  @DisplayName("getSalesTarget - 기존 타겟이 존재하지 않을 경우 (초기 진입)")
  @Test
  void getSalesTarget_기존타겟없음_성공() {
    // given
    Long shopId = 1L;
    LocalDate targetDate = LocalDate.of(2024, 7, 1);
    GetSalesTargetRequest request = new GetSalesTargetRequest(targetDate);

    // 직원 목록 (2명)
    Staff staff1 = Staff.builder().staffId(300L).staffName("신입A").build();
    Staff staff2 = Staff.builder().staffId(301L).staffName("신입B").build();
    given(userRepository.findByShopIdAndLeftDateIsNull(shopId)).willReturn(List.of(staff1, staff2));

    // 해당 월의 타겟 설정 없음
    given(salesTargetQueryRepository.findAppliedTargets(eq(shopId), any(), any()))
        .willReturn(List.of());

    // when
    SalesTargetListResult result = service.getSalesTarget(shopId, request);

    // then
    assertThat(result.getType()).isEqualTo(StaffSalesSettingType.BULK); // 기본적으로 BULK
    assertThat(result.getSalesTargetInfos()).hasSize(2);

    for (StaffSalesTargetInfo info : result.getSalesTargetInfos()) {
      assertThat(info.getTargetAmounts().getOrDefault("items", 0)).isEqualTo(0);
      assertThat(info.getTargetAmounts().getOrDefault("membership", 0)).isEqualTo(0);
    }
  }
}
