package com.deveagles.be15_deveagles_be.features.schedules.command.application.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.*;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.*;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository.PlanRepository;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository.RegularPlanRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("일정 command 테스트")
class PlanCommandServiceTest {

  @InjectMocks private PlanCommandService planCommandService;

  @Mock private PlanRepository planRepository;

  @Mock private RegularPlanRepository regularPlanRepository;

  @Test
  @DisplayName("단기 일정 등록 성공")
  void createPlan_success() {
    CreatePlanRequest request =
        new CreatePlanRequest(
            1L,
            1L,
            "회의",
            "정기 회의",
            LocalDateTime.of(2025, 6, 25, 10, 0),
            LocalDateTime.of(2025, 6, 25, 12, 0));

    Plan savedPlan =
        Plan.builder()
            .planId(100L)
            .staffId(request.staffId())
            .shopId(request.shopId())
            .planTitle(request.planTitle())
            .planMemo(request.planMemo())
            .planStartAt(request.planStartAt())
            .planEndAt(request.planEndAt())
            .build();

    when(planRepository.save(any(Plan.class))).thenReturn(savedPlan);

    Long result = planCommandService.createPlan(request);

    assertThat(result).isEqualTo(100L);
    verify(planRepository).save(any(Plan.class));
  }

  @Test
  @DisplayName("정기 일정(주) 등록 성공")
  void createRegularPlanWeekly_success() {
    CreateRegularPlanRequest request =
        new CreateRegularPlanRequest(
            1L,
            1L,
            "매주 회의",
            null,
            DayOfWeekEnum.MON,
            "주간 회의",
            LocalTime.of(10, 0),
            LocalTime.of(11, 30));

    RegularPlan saved =
        RegularPlan.builder()
            .regularPlanId(200L)
            .staffId(request.staffId())
            .shopId(request.shopId())
            .regularPlanTitle(request.regularPlanTitle())
            .monthlyPlan(request.monthlyPlan())
            .weeklyPlan(request.weeklyPlan())
            .regularPlanMemo(request.regularPlanMemo())
            .regularPlanStartAt(request.regularPlanStartAt())
            .regularPlanEndAt(request.regularPlanEndAt())
            .build();

    when(regularPlanRepository.save(any(RegularPlan.class))).thenReturn(saved);

    Long result = planCommandService.createRegularPlan(request);

    assertThat(result).isEqualTo(200L);
    verify(regularPlanRepository).save(any(RegularPlan.class));
  }

  @Test
  @DisplayName("정기 일정(월) 등록 성공")
  void createRegularPlanMonthly_success() {
    CreateRegularPlanRequest request =
        new CreateRegularPlanRequest(
            1L, 1L, "매달 회의", 15, null, "월간 회의", LocalTime.of(10, 0), LocalTime.of(11, 30));

    RegularPlan saved =
        RegularPlan.builder()
            .regularPlanId(200L)
            .staffId(request.staffId())
            .shopId(request.shopId())
            .regularPlanTitle(request.regularPlanTitle())
            .monthlyPlan(request.monthlyPlan())
            .weeklyPlan(request.weeklyPlan())
            .regularPlanMemo(request.regularPlanMemo())
            .regularPlanStartAt(request.regularPlanStartAt())
            .regularPlanEndAt(request.regularPlanEndAt())
            .build();

    when(regularPlanRepository.save(any(RegularPlan.class))).thenReturn(saved);

    Long result = planCommandService.createRegularPlan(request);

    assertThat(result).isEqualTo(200L);
    verify(regularPlanRepository).save(any(RegularPlan.class));
  }

  @Test
  @DisplayName("plan + regular 다건 삭제 성공")
  void deleteMixedSchedules_success() {
    List<DeleteScheduleRequest> requests =
        List.of(new DeleteScheduleRequest(1L, "plan"), new DeleteScheduleRequest(2L, "regular"));

    Plan p = Plan.builder().planId(1L).build();
    RegularPlan r = RegularPlan.builder().regularPlanId(2L).build();

    when(planRepository.findAllById(List.of(1L))).thenReturn(List.of(p));
    when(regularPlanRepository.findAllById(List.of(2L))).thenReturn(List.of(r));

    planCommandService.deleteMixedSchedules(requests);

    verify(planRepository).deleteAllInBatch(List.of(p));
    verify(regularPlanRepository).deleteAllInBatch(List.of(r));
  }

  @Test
  @DisplayName("plan 일부 없으면 예외")
  void deleteMixedSchedules_planNotFound() {
    List<DeleteScheduleRequest> requests = List.of(new DeleteScheduleRequest(1L, "plan"));

    when(planRepository.findAllById(List.of(1L))).thenReturn(List.of());

    assertThatThrownBy(() -> planCommandService.deleteMixedSchedules(requests))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.PLAN_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("regular 일부 없으면 예외")
  void deleteMixedSchedules_regularNotFound() {
    List<DeleteScheduleRequest> requests = List.of(new DeleteScheduleRequest(2L, "regular"));

    when(regularPlanRepository.findAllById(List.of(2L))).thenReturn(List.of());

    assertThatThrownBy(() -> planCommandService.deleteMixedSchedules(requests))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.REGULAR_PLAN_NOT_FOUND.getMessage());
  }

  @Test
  @DisplayName("잘못된 타입이면 예외")
  void deleteMixedSchedules_invalidType() {
    List<DeleteScheduleRequest> requests = List.of(new DeleteScheduleRequest(3L, "invalid"));

    assertThatThrownBy(() -> planCommandService.deleteMixedSchedules(requests))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.INVALID_SCHEDULE_TYPE.getMessage());
  }

  @Test
  @DisplayName("단기 일정 시간역순이면 예외")
  void createPlan_invalidTimeRange() {
    CreatePlanRequest request =
        new CreatePlanRequest(
            1L,
            1L,
            "오류",
            "시간 역순",
            LocalDateTime.of(2025, 6, 25, 14, 0),
            LocalDateTime.of(2025, 6, 25, 10, 0));

    assertThatThrownBy(() -> planCommandService.createPlan(request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.INVALID_RESERVATION_TIME_RANGE.getMessage());
  }

  @Test
  @DisplayName("정기 일정 반복 설정 오류")
  void createRegularPlan_repeatTypeConflict() {
    CreateRegularPlanRequest bothNull =
        new CreateRegularPlanRequest(
            1L, 1L, "없음", null, null, "메모", LocalTime.of(9, 0), LocalTime.of(10, 0));
    CreateRegularPlanRequest bothExist =
        new CreateRegularPlanRequest(
            1L, 1L, "둘다", 15, DayOfWeekEnum.FRI, "메모", LocalTime.of(9, 0), LocalTime.of(10, 0));

    assertThatThrownBy(() -> planCommandService.createRegularPlan(bothNull))
        .isInstanceOf(BusinessException.class);

    assertThatThrownBy(() -> planCommandService.createRegularPlan(bothExist))
        .isInstanceOf(BusinessException.class);
  }

  @Test
  @DisplayName("같은 타입이면 내부 수정 호출")
  void switchSchedule_sameType_planToPlan_success() {
    Long planId = 1L;
    CreatePlanRequest request =
        new CreatePlanRequest(
            1L,
            1L,
            "수정제목",
            "수정메모",
            LocalDateTime.of(2025, 6, 25, 11, 0),
            LocalDateTime.of(2025, 6, 25, 12, 0));

    Plan origin =
        Plan.builder()
            .planId(planId)
            .staffId(1L)
            .shopId(1L)
            .planTitle("기존제목")
            .planMemo("기존메모")
            .planStartAt(LocalDateTime.of(2025, 6, 25, 9, 0))
            .planEndAt(LocalDateTime.of(2025, 6, 25, 10, 0))
            .build();

    when(planRepository.findById(planId)).thenReturn(java.util.Optional.of(origin));

    planCommandService.switchSchedule(
        new UpdateScheduleRequest(ScheduleType.PLAN, planId, ScheduleType.PLAN, request, null));

    assertThat(origin.getPlanTitle()).isEqualTo("수정제목");
    verify(planRepository).findById(planId);
  }

  @Test
  @DisplayName("정기 → 단기 전환")
  void switchSchedule_regularToPlan_success() {
    Long id = 2L;

    CreatePlanRequest request =
        new CreatePlanRequest(
            1L,
            1L,
            "단기",
            "전환됨",
            LocalDateTime.of(2025, 6, 26, 13, 0),
            LocalDateTime.of(2025, 6, 26, 14, 0));

    when(regularPlanRepository.existsById(id)).thenReturn(true);
    when(planRepository.save(any(Plan.class))).thenReturn(Plan.builder().planId(999L).build());

    planCommandService.switchSchedule(
        new UpdateScheduleRequest(ScheduleType.REGULAR, id, ScheduleType.PLAN, request, null));

    verify(regularPlanRepository).deleteById(id);
    verify(planRepository).save(any(Plan.class));
  }

  @Test
  @DisplayName("단기 → 정기 전환")
  void switchSchedule_planToRegular_success() {
    Long id = 1L;

    CreateRegularPlanRequest request =
        new CreateRegularPlanRequest(
            1L, 1L, "정기", null, DayOfWeekEnum.WED, "메모", LocalTime.of(9, 0), LocalTime.of(10, 0));

    when(planRepository.existsById(id)).thenReturn(true);
    when(regularPlanRepository.save(any(RegularPlan.class)))
        .thenReturn(RegularPlan.builder().regularPlanId(888L).build());

    planCommandService.switchSchedule(
        new UpdateScheduleRequest(ScheduleType.PLAN, id, ScheduleType.REGULAR, null, request));

    verify(planRepository).deleteById(id);
    verify(regularPlanRepository).save(any(RegularPlan.class));
  }
}
