package com.deveagles.be15_deveagles_be.features.schedules.query.mapper;

import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.CalendarScheduleRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.CalendarScheduleResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CalendarScheduleQueryMapper {
  List<CalendarScheduleResponse> findSchedules(
      @Param("shopId") Long shopId, @Param("request") CalendarScheduleRequest request);
}
