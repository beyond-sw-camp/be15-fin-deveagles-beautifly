package com.deveagles.be15_deveagles_be.features.schedules.query.service;

import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.BookedTimeRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.BookedTimeDto;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.BookedTimeResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.mapper.ReservationQueryMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReservationQueryService {

  private final ReservationQueryMapper reservationQueryMapper;

  public BookedTimeResponse getBookedTimes(BookedTimeRequest req) {
    List<BookedTimeDto> results = reservationQueryMapper.findBookedTimes(req);
    return new BookedTimeResponse(results);
  }
}
