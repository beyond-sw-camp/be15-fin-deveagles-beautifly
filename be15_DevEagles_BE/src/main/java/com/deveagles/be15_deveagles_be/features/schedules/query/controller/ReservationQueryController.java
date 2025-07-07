package com.deveagles.be15_deveagles_be.features.schedules.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.BookedTimeRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.request.ReservationSearchRequest;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.BookedTimeResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.ReservationDetailResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.ReservationListResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.ReservationSearchResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.service.ReservationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules/reservations")
@Tag(name = "예약 조회", description = "예약 및 시간 관련 조회 API")
public class ReservationQueryController {

  private final ReservationQueryService reservationQueryService;

  @Operation(
      summary = "직원의 예약된 시간 조회",
      description = "특정 날짜 기준으로 직원의 예약된 시간을 조회합니다. 해당 시간은 예약 불가 시간입니다.")
  @GetMapping("/staff/{staffId}/available-times")
  public ResponseEntity<ApiResponse<BookedTimeResponse>> getBookedTimes(
      @PathVariable Long staffId,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    BookedTimeRequest request = new BookedTimeRequest(staffId, date);
    BookedTimeResponse data = reservationQueryService.getBookedTimes(request);
    return ResponseEntity.ok(ApiResponse.success(data));
  }

  @Operation(summary = "예약 신청 목록 조회", description = "매장의 PENDING 상태 예약 신청 목록을 조회합니다.")
  @GetMapping("/requests")
  public ApiResponse<PagedResponse<ReservationListResponse>> findReservationRequests(
      @RequestParam Long shopId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    return ApiResponse.success(
        PagedResponse.from(reservationQueryService.findReservationRequests(shopId, page, size)));
  }

  @Operation(summary = "예약 전체 조회", description = "담당자, 상태, 고객 키워드로 예약 전체를 조회합니다.")
  @GetMapping
  public ApiResponse<PagedResponse<ReservationSearchResponse>> searchReservations(
      ReservationSearchRequest request,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    var result = reservationQueryService.searchReservations(request, page, size);
    return ApiResponse.success(PagedResponse.from(result));
  }

  @Operation(summary = "예약 상세 조회", description = "예약 ID로 상세 정보를 조회합니다.")
  @GetMapping("/{reservationId}")
  public ApiResponse<ReservationDetailResponse> getReservationDetail(
      @AuthenticationPrincipal CustomUser user, @PathVariable Long reservationId) {
    Long shopId = user.getShopId();
    ReservationDetailResponse response =
        reservationQueryService.getReservationDetail(reservationId, shopId);
    return ApiResponse.success(response);
  }
}
