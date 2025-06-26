package com.deveagles.be15_deveagles_be.features.schedules.command.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.dto.request.*;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.*;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository.LeaveRepository;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.repository.RegularLeaveRepository;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

public class LeaveCommandServiceTest {

  @Mock private LeaveRepository leaveRepository;

  @Mock private RegularLeaveRepository regularLeaveRepository;

  @InjectMocks private LeaveCommandService leaveCommandService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("단기 휴무 생성 성공")
  void createLeave_success() {
    CreateLeaveRequest req = new CreateLeaveRequest(1L, 1L, "개인 사정", LocalDate.now(), "메모");
    Leave saved = Leave.builder().leaveId(100L).build();
    when(leaveRepository.save(any())).thenReturn(saved);

    Long result = leaveCommandService.createLeave(req);

    assertThat(result).isEqualTo(100L);
    verify(leaveRepository).save(any());
  }

  @Test
  @DisplayName("단기 휴무 수정 성공")
  void updateLeave_success() {
    UpdateLeaveRequest req = new UpdateLeaveRequest("수정 제목", LocalDate.now(), "수정 메모");
    Leave mockLeave = mock(Leave.class);
    when(leaveRepository.findById(1L)).thenReturn(Optional.of(mockLeave));

    leaveCommandService.updateLeave(1L, req);

    verify(mockLeave).update(req.leaveTitle(), req.leaveAt(), req.leaveMemo());
  }

  @Test
  @DisplayName("단기 휴무 수정 실패 - 존재하지 않는 ID")
  void updateLeave_notFound() {
    when(leaveRepository.findById(1L)).thenReturn(Optional.empty());

    BusinessException ex =
        assertThrows(
            BusinessException.class,
            () ->
                leaveCommandService.updateLeave(
                    1L, new UpdateLeaveRequest("제목", LocalDate.now(), "메모")));

    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.LEAVE_NOT_FOUND);
  }

  @Test
  @DisplayName("정기 휴무 생성 실패 - 요일과 월 중복 설정")
  void createRegularLeave_bothSet_shouldFail() {
    CreateRegularLeaveRequest req =
        new CreateRegularLeaveRequest(1L, 1L, "중복", 5, DayOfWeekEnum.MON, "메모");

    BusinessException ex =
        assertThrows(BusinessException.class, () -> leaveCommandService.createRegularLeave(req));

    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_SCHEDULE_REPEAT_TYPE);
  }

  @Test
  @DisplayName("정기 휴무 생성 실패 - 요일과 월 모두 없음")
  void createRegularLeave_noneSet_shouldFail() {
    CreateRegularLeaveRequest req = new CreateRegularLeaveRequest(1L, 1L, "없음", null, null, "메모");

    BusinessException ex =
        assertThrows(BusinessException.class, () -> leaveCommandService.createRegularLeave(req));

    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_SCHEDULE_REPEAT_TYPE);
  }

  @Test
  @DisplayName("정기 휴무 생성 성공")
  void createRegularLeave_success() {
    CreateRegularLeaveRequest req =
        new CreateRegularLeaveRequest(1L, 1L, "정기휴무", null, DayOfWeekEnum.MON, "메모");
    RegularLeave saved = RegularLeave.builder().regularLeaveId(200L).build();
    when(regularLeaveRepository.save(any())).thenReturn(saved);

    Long result = leaveCommandService.createRegularLeave(req);

    assertThat(result).isEqualTo(200L);
  }

  @Test
  @DisplayName("정기 휴무 수정 실패 - 존재하지 않는 ID")
  void updateRegularLeave_notFound() {
    when(regularLeaveRepository.findById(999L)).thenReturn(Optional.empty());

    BusinessException ex =
        assertThrows(
            BusinessException.class,
            () ->
                leaveCommandService.updateRegularLeave(
                    999L, new UpdateRegularLeaveRequest("제목", null, DayOfWeekEnum.FRI, "메모")));

    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.REGULAR_LEAVE_NOT_FOUND);
  }

  @Test
  @DisplayName("단기 + 정기 휴무 혼합 삭제 성공")
  void deleteMixedLeaves_success() {
    List<DeleteScheduleRequest> reqs =
        List.of(new DeleteScheduleRequest(1L, "leave"), new DeleteScheduleRequest(2L, "regular"));
    List<Leave> leaveList = List.of(mock(Leave.class));
    List<RegularLeave> regularList = List.of(mock(RegularLeave.class));

    when(leaveRepository.findAllById(List.of(1L))).thenReturn(leaveList);
    when(regularLeaveRepository.findAllById(List.of(2L))).thenReturn(regularList);

    leaveCommandService.deleteMixedLeaves(reqs);

    verify(leaveRepository).deleteAllInBatch(leaveList);
    verify(regularLeaveRepository).deleteAllInBatch(regularList);
  }

  @Test
  @DisplayName("휴무 삭제 실패 - 잘못된 타입")
  void deleteMixedLeaves_invalidType() {
    List<DeleteScheduleRequest> reqs = List.of(new DeleteScheduleRequest(99L, "invalid"));

    BusinessException ex =
        assertThrows(BusinessException.class, () -> leaveCommandService.deleteMixedLeaves(reqs));

    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_SCHEDULE_TYPE);
  }

  @Test
  @DisplayName("휴무 삭제 실패 - 단기 휴무 일부 누락")
  void deleteMixedLeaves_leaveNotFound() {
    List<DeleteScheduleRequest> reqs = List.of(new DeleteScheduleRequest(1L, "leave"));
    when(leaveRepository.findAllById(List.of(1L))).thenReturn(List.of()); // 빈 목록 반환

    BusinessException ex =
        assertThrows(BusinessException.class, () -> leaveCommandService.deleteMixedLeaves(reqs));

    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.LEAVE_NOT_FOUND);
  }

  @Test
  @DisplayName("휴무 삭제 실패 - 정기 휴무 일부 누락")
  void deleteMixedLeaves_regularNotFound() {
    List<DeleteScheduleRequest> reqs = List.of(new DeleteScheduleRequest(2L, "regular"));
    when(regularLeaveRepository.findAllById(List.of(2L))).thenReturn(List.of());

    BusinessException ex =
        assertThrows(BusinessException.class, () -> leaveCommandService.deleteMixedLeaves(reqs));

    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.REGULAR_LEAVE_NOT_FOUND);
  }
}
