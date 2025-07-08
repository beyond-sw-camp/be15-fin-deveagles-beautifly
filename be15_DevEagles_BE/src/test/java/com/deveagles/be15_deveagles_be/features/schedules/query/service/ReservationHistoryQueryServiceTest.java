package com.deveagles.be15_deveagles_be.features.schedules.query.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.common.dto.Pagination;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.ReservationHistoryResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.mapper.ReservationHistoryMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ReservationHistoryQueryServiceTest {

  @Mock private ReservationHistoryMapper reservationHistoryMapper;

  @InjectMocks private ReservationHistoryQueryService reservationHistoryQueryService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void getReservationHistories_정상조회_페이징_응답검증() {
    // given
    Long shopId = 1L;
    int page = 0;
    int size = 2;
    int offset = 0;

    List<ReservationHistoryResponse> mockContent =
        List.of(
            new ReservationHistoryResponse(
                100L,
                "김미글",
                "염색, 커트",
                "박미글",
                LocalDateTime.of(2025, 6, 8, 14, 0),
                "수정",
                LocalDateTime.of(2025, 6, 8, 10, 30)),
            new ReservationHistoryResponse(
                101L,
                "이아름",
                "컷",
                "최아름",
                LocalDateTime.of(2025, 6, 9, 15, 0),
                "삭제",
                LocalDateTime.of(2025, 6, 9, 11, 0)));

    when(reservationHistoryMapper.getReservationHistoryList(shopId, size, offset))
        .thenReturn(mockContent);
    when(reservationHistoryMapper.countReservationHistories(shopId)).thenReturn(5L);

    // when
    PagedResponse<ReservationHistoryResponse> result =
        reservationHistoryQueryService.getReservationHistories(shopId, page, size);

    // then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getPagination()).isNotNull();

    Pagination pagination = result.getPagination();
    assertThat(pagination.getCurrentPage()).isEqualTo(0);
    assertThat(pagination.getTotalItems()).isEqualTo(5);
    assertThat(pagination.getTotalPages()).isEqualTo(3);
  }
}
