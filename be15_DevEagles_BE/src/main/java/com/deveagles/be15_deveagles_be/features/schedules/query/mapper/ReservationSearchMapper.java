package com.deveagles.be15_deveagles_be.features.schedules.query.mapper;

import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.ReservationSearchRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.ReservationSearchResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReservationSearchMapper {

  List<ReservationSearchResponse> findReservations(
      @Param("req") ReservationSearchRequest req,
      @Param("offset") int offset,
      @Param("size") int size);

  int countReservations(@Param("req") ReservationSearchRequest req);
}
