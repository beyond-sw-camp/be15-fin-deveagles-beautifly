package com.deveagles.be15_deveagles_be.features.schedules.query.mapper;

import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.ReservationHistoryResponse;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReservationHistoryMapper {

  List<ReservationHistoryResponse> getReservationHistoryList(
      @Param("shopId") Long shopId, @Param("limit") int limit, @Param("offset") int offset);

  long countReservationHistories(@Param("shopId") Long shopId);
}
