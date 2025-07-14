package com.deveagles.be15_deveagles_be.features.schedules.query.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ScheduleType;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.CalendarRegularRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.CalendarRegularResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.CalendarRenderedResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.mapper.CalendarScheduleQueryMapper;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CalendarScheduleQueryServiceTest {

  @Mock private CalendarScheduleQueryMapper calendarScheduleQueryMapper;

  @InjectMocks private CalendarScheduleQueryService calendarScheduleQueryService;

  private CalendarRegularResponse regularSchedule;

  @BeforeEach
  void setup() {
    regularSchedule =
        new CalendarRegularResponse(
            1L,
            ScheduleType.REGULAR_PLAN,
            "정기미팅",
            "이채은",
            "매주 화요일 회의",
            "#FF9900",
            "TUE", // weeklyRepeatDay
            null, // monthlyRepeatDay
            "10:00:00",
            "11:00:00");
  }

  @Test
  void 정기일정이_요일기준으로_정상적으로_확장되는지_검증() {
    // given
    LocalDate from = LocalDate.of(2025, 7, 1);
    LocalDate to = LocalDate.of(2025, 7, 10);
    CalendarRegularRequest request =
        new CalendarRegularRequest(ScheduleType.REGULAR_PLAN, from, to, 1L);

    when(calendarScheduleQueryMapper.findRegularSchedules(1L, request))
        .thenReturn(List.of(regularSchedule));

    // when
    List<CalendarRenderedResponse> result =
        calendarScheduleQueryService.getExpandedRegularSchedules(1L, request);

    // then
    assertThat(result).hasSizeGreaterThanOrEqualTo(1);
    assertThat(result.get(0).title()).isEqualTo("정기미팅");
    assertThat(result.get(0).scheduleType()).isEqualTo(ScheduleType.REGULAR_PLAN);
    assertThat(result.get(0).staffName()).isEqualTo("이채은");
  }
}
