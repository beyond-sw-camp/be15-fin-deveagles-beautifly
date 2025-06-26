package com.deveagles.be15_deveagles_be.features.schedules.query.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.common.dto.Pagination;
import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.LeaveListRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.LeaveDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.LeaveListResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.RegularLeaveDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.mapper.LeaveQueryMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class LeaveQueryServiceTest {

  @Mock private LeaveQueryMapper leaveQueryMapper;

  @InjectMocks private LeaveQueryService leaveQueryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("휴무 목록 조회 성공")
  void getLeaveList_success() {
    LeaveListRequest request =
        new LeaveListRequest(
            1L, LocalDate.of(2025, 6, 1), LocalDate.of(2025, 6, 30), "leave", 2L, 0, 10);

    List<LeaveListResponse> dummyList =
        List.of(new LeaveListResponse(1L, "김민지", "휴무", "leave", null, LocalDate.of(2025, 6, 10)));

    when(leaveQueryMapper.findLeaves(eq(1L), any(), any(), eq("leave"), eq(2L), eq(10), eq(0)))
        .thenReturn(dummyList);

    when(leaveQueryMapper.countLeaves(eq(1L), any(), any(), eq("leave"), eq(2L))).thenReturn(1);

    PagedResult<LeaveListResponse> result = leaveQueryService.getLeaveList(request);

    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).staffName()).isEqualTo("김민지");

    Pagination pagination = result.getPagination();
    assertThat(pagination.getTotalItems()).isEqualTo(1);
    assertThat(pagination.getTotalPages()).isEqualTo(1);
    assertThat(pagination.getCurrentPage()).isEqualTo(0);
  }

  @Test
  @DisplayName("단기 휴무 상세 조회 성공")
  void getLeaveDetail_success() {
    Long leaveId = 100L;
    LeaveDetailResponse dummy =
        new LeaveDetailResponse(leaveId, 2L, "홍길동", "병가", LocalDate.of(2025, 6, 25), "병원 방문");

    when(leaveQueryMapper.findLeaveDetailById(leaveId)).thenReturn(dummy);

    LeaveDetailResponse result = leaveQueryService.getLeaveDetail(leaveId);

    assertThat(result.leaveId()).isEqualTo(leaveId);
    assertThat(result.leaveTitle()).isEqualTo("병가");
    assertThat(result.memo()).isEqualTo("병원 방문");
  }

  @Test
  @DisplayName("정기 휴무 상세 조회 성공")
  void getRegularLeaveDetail_success() {
    Long regularLeaveId = 200L;
    RegularLeaveDetailResponse dummy =
        new RegularLeaveDetailResponse(regularLeaveId, 3L, "이채은", "정기휴무", "매주 수요일", "사유 있음");

    when(leaveQueryMapper.findRegularLeaveDetailById(regularLeaveId)).thenReturn(dummy);

    RegularLeaveDetailResponse result = leaveQueryService.getRegularLeaveDetail(regularLeaveId);

    assertThat(result.regularLeaveId()).isEqualTo(regularLeaveId);
    assertThat(result.staffName()).isEqualTo("이채은");
    assertThat(result.repeatRule()).isEqualTo("매주 수요일");
  }

  @Test
  @DisplayName("단기 휴무 상세 조회 실패 - 존재하지 않음")
  void getLeaveDetail_notFound() {
    Long leaveId = 999L;
    when(leaveQueryMapper.findLeaveDetailById(leaveId)).thenReturn(null);

    BusinessException ex =
        assertThrows(BusinessException.class, () -> leaveQueryService.getLeaveDetail(leaveId));

    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.LEAVE_NOT_FOUND);
  }

  @Test
  @DisplayName("정기 휴무 상세 조회 실패 - 존재하지 않음")
  void getRegularLeaveDetail_notFound() {
    Long regularLeaveId = 888L;
    when(leaveQueryMapper.findRegularLeaveDetailById(regularLeaveId)).thenReturn(null);

    BusinessException ex =
        assertThrows(
            BusinessException.class, () -> leaveQueryService.getRegularLeaveDetail(regularLeaveId));

    assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.REGULAR_LEAVE_NOT_FOUND);
  }
}
