package com.deveagles.be15_deveagles_be.features.schedules.query.mapper;

import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.BookedTimeRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.BookedTimeDto;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.ReservationDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.ReservationListResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReservationQueryMapper {
  List<BookedTimeDto> findBookedTimes(@Param("req") BookedTimeRequest req);

  List<ReservationListResponse> findReservationRequests(
      @Param("shopId") Long shopId, @Param("limit") int limit, @Param("offset") int offset);

  long countReservationRequests(@Param("shopId") Long shopId);

  ReservationDetailResponse findReservationDetailById(
      @Param("reservationId") Long reservationId, @Param("shopId") Long shopId);
}
