package com.deveagles.be15_deveagles_be.features.schedules.query.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.BookedTimeRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.BookedTimeDto;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.BookedTimeResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.mapper.ReservationQueryMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("예약된 시간 조회 서비스 테스트")
class ReservationQueryServiceTest {

  @Mock private ReservationQueryMapper reservationQueryMapper;

  @InjectMocks private ReservationQueryService reservationQueryService;

  @Test
  void 예약된_시간목록을_조회할_수_있다() {
    // given
    BookedTimeRequest request = new BookedTimeRequest(2L, LocalDate.of(2025, 7, 1));
    List<BookedTimeDto> mockResult =
        List.of(new BookedTimeDto("10:00"), new BookedTimeDto("13:30"));

    when(reservationQueryMapper.findBookedTimes(request)).thenReturn(mockResult);

    // when
    BookedTimeResponse result = reservationQueryService.getBookedTimes(request);

    // then
    assertThat(result).isNotNull();
    assertThat(result.bookedTimes()).hasSize(2);
    assertThat(result.bookedTimes().get(0).time()).isEqualTo("10:00");

    verify(reservationQueryMapper, times(1)).findBookedTimes(request);
  }
}
