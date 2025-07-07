package com.deveagles.be15_deveagles_be.features.schedules.query.service;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.common.dto.Pagination;
import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.BookedTimeRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.ReservationSearchRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.*;
import com.deveagles.be15_deveagles_be.features.schedules.query.mapper.ReservationQueryMapper;
import com.deveagles.be15_deveagles_be.features.schedules.query.mapper.ReservationSearchMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationQueryService {

  private final ReservationQueryMapper reservationQueryMapper;
  private final ReservationSearchMapper reservationSearchMapper;

  public BookedTimeResponse getBookedTimes(BookedTimeRequest req) {
    List<BookedTimeDto> results = reservationQueryMapper.findBookedTimes(req);
    return new BookedTimeResponse(results);
  }

  public PagedResult<ReservationListResponse> findReservationRequests(
      Long shopId, int page, int size) {
    int offset = page * size;
    List<ReservationListResponse> content =
        reservationQueryMapper.findReservationRequests(shopId, size, offset);
    long totalCount = reservationQueryMapper.countReservationRequests(shopId);

    Pagination pagination =
        Pagination.builder()
            .currentPage(page)
            .totalPages((int) Math.ceil((double) totalCount / size))
            .totalItems(totalCount)
            .build();

    return new PagedResult<>(content, pagination);
  }

  public PagedResult<ReservationSearchResponse> searchReservations(
      Long shopId, ReservationSearchRequest request, int page, int size) {

    int offset = page * size;

    ReservationSearchRequest newRequest =
        ReservationSearchRequest.builder()
            .shopId(shopId)
            .staffId(request.staffId())
            .reservationStatusName(request.reservationStatusName())
            .customerKeyword(request.customerKeyword())
            .from(request.from())
            .to(request.to())
            .build();

    List<ReservationSearchResponse> content =
        reservationSearchMapper.findReservations(newRequest, offset, size);

    int totalCount = reservationSearchMapper.countReservations(newRequest);

    return new PagedResult<>(
        content, new Pagination(page, (int) Math.ceil((double) totalCount / size), totalCount));
  }

  public ReservationDetailResponse getReservationDetail(Long reservationId, Long shopId) {
    ReservationDetailResponse response =
        reservationQueryMapper.findReservationDetailById(reservationId, shopId);
    if (response == null) {
      throw new BusinessException(ErrorCode.RESERVATION_NOT_FOUND);
    }
    return response;
  }
}
