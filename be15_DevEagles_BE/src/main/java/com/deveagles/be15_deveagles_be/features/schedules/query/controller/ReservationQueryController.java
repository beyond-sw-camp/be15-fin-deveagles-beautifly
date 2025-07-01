package com.deveagles.be15_deveagles_be.features.schedules.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.BookedTimeRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.BookedTimeResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.service.ReservationQueryService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules/reservations")
public class ReservationQueryController {

  private final ReservationQueryService reservationQueryService;

  @GetMapping("/staff/{staffId}/available-times")
  public ResponseEntity<ApiResponse<BookedTimeResponse>> getBookedTimes(
      @PathVariable Long staffId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    BookedTimeRequest request = new BookedTimeRequest(staffId, date);
    BookedTimeResponse data = reservationQueryService.getBookedTimes(request);
    return ResponseEntity.ok(ApiResponse.success(data));
  }
}
