package com.deveagles.be15_deveagles_be.features.schedules.query.service;

import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.CalendarScheduleRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.CalendarScheduleResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.mapper.CalendarScheduleQueryMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CalendarScheduleQueryService {

  private final CalendarScheduleQueryMapper calendarScheduleQueryMapper;

  public List<CalendarScheduleResponse> findSchedules(
      Long shopId, CalendarScheduleRequest request) {
    return calendarScheduleQueryMapper.findSchedules(shopId, request);
  }
}
