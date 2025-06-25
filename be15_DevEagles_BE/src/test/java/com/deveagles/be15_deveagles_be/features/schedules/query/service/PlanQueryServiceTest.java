package com.deveagles.be15_deveagles_be.features.schedules.query.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.PlanDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.PlanListResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.RegularPlanDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.mapper.PlanQueryMapper;
import com.deveagles.be15_deveagles_be.features.schedules.query.mapper.RegularPlanQueryMapper;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("일정 상세/목록 조회 서비스 테스트")
class PlanQueryServiceTest {

  @InjectMocks private PlanQueryService scheduleQueryService;

  @Mock private PlanQueryMapper planQueryMapper;

  @Mock private RegularPlanQueryMapper regularPlanQueryMapper;

  @Test
  @DisplayName("단기 일정 상세 조회 성공")
  void getPlanDetail_success() {
    Long planId = 1L;
    PlanDetailResponse mockResponse =
        new PlanDetailResponse(
            planId,
            1L,
            1L,
            "회의",
            "상세내용",
            LocalDateTime.of(2025, 6, 25, 10, 0),
            LocalDateTime.of(2025, 6, 25, 11, 0));
    when(planQueryMapper.findPlanDetailById(planId)).thenReturn(mockResponse);

    PlanDetailResponse result = scheduleQueryService.getPlanDetail(planId);

    assertThat(result).isNotNull();
    assertThat(result.planId()).isEqualTo(planId);
    verify(planQueryMapper).findPlanDetailById(planId);
  }

  @Test
  @DisplayName("단기 일정이 존재하지 않으면 예외 발생")
  void getPlanDetail_notFound() {
    Long planId = 99L;
    when(planQueryMapper.findPlanDetailById(planId)).thenReturn(null);

    assertThatThrownBy(() -> scheduleQueryService.getPlanDetail(planId))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.PLAN_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("정기 일정 상세 조회 성공")
  void getRegularPlanDetail_success() {
    Long regularId = 2L;
    RegularPlanDetailResponse mockResponse =
        new RegularPlanDetailResponse(
            regularId,
            1L,
            1L,
            "매주 미팅",
            "메모",
            null,
            "MON",
            LocalTime.of(10, 0),
            LocalTime.of(11, 0));
    when(regularPlanQueryMapper.findRegularPlanDetailById(regularId)).thenReturn(mockResponse);

    RegularPlanDetailResponse result = scheduleQueryService.getRegularPlanDetail(regularId);

    assertThat(result).isNotNull();
    assertThat(result.regularPlanId()).isEqualTo(regularId);
    verify(regularPlanQueryMapper).findRegularPlanDetailById(regularId);
  }

  @Test
  @DisplayName("정기 일정이 존재하지 않으면 예외 발생")
  void getRegularPlanDetail_notFound() {
    Long regularId = 77L;
    when(regularPlanQueryMapper.findRegularPlanDetailById(regularId)).thenReturn(null);

    assertThatThrownBy(() -> scheduleQueryService.getRegularPlanDetail(regularId))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.REGULAR_PLAN_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("단기/정기 일정 목록 조회 성공")
  void getPlanList_success() {
    Long staffId = 1L;
    Long shopId = 1L;
    String planType = "all";
    int page = 0;
    int size = 10;
    int offset = 0;

    List<PlanListResponse> mockList =
        List.of(
            new PlanListResponse(
                1L, "plan", "회의", "메모", "김민지", LocalDateTime.now(), LocalDateTime.now()),
            new PlanListResponse(
                2L, "regular", "정기미팅", "반복", "김민지", LocalDateTime.now(), LocalDateTime.now()));

    when(planQueryMapper.findPlans(anyLong(), anyLong(), anyString(), anyInt(), anyInt()))
        .thenReturn(mockList);
    when(planQueryMapper.countPlans(anyLong(), anyLong(), anyString())).thenReturn(mockList.size());

    var result = scheduleQueryService.getPlanList(staffId, shopId, planType, page, size);

    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getPagination().getTotalItems()).isEqualTo(2L);
    verify(planQueryMapper).findPlans(staffId, shopId, planType, offset, size);
    verify(planQueryMapper).countPlans(staffId, shopId, planType);
  }
}
