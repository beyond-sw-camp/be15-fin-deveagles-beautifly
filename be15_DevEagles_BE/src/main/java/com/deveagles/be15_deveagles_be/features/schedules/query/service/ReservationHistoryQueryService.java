package com.deveagles.be15_deveagles_be.features.schedules.query.service;

import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.common.dto.Pagination;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.ReservationHistoryResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.mapper.ReservationHistoryMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationHistoryQueryService {

  private final ReservationHistoryMapper reservationHistoryMapper;

  public PagedResponse<ReservationHistoryResponse> getReservationHistories(
      Long shopId, int page, int size) {
    int offset = page * size;

    List<ReservationHistoryResponse> content =
        reservationHistoryMapper.getReservationHistoryList(shopId, size, offset);

    long total = reservationHistoryMapper.countReservationHistories(shopId);

    Pagination pagination =
        Pagination.builder()
            .currentPage(page)
            .totalPages((int) Math.ceil((double) total / size))
            .totalItems(total)
            .build();

    return PagedResponse.from(new PagedResult<>(content, pagination));
  }
}
