package com.deveagles.be15_deveagles_be.features.schedules.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.schedules.query.dto.response.ReservationHistoryResponse;
import com.deveagles.be15_deveagles_be.features.schedules.query.service.ReservationHistoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "예약 변경 이력", description = "예약 변경 이력 관련 API")
@RestController
@RequestMapping("/schedules/reservations")
@RequiredArgsConstructor
public class ReservationHistoryQueryController {

  private final ReservationHistoryQueryService reservationHistoryQueryService;

  @Operation(summary = "예약 변경 이력 조회", description = "예약의 마지막 등록/수정/삭제 이력을 최신순으로 조회합니다.")
  @GetMapping("/history")
  public ResponseEntity<ApiResponse<PagedResponse<ReservationHistoryResponse>>>
      getReservationHistories(
          @AuthenticationPrincipal CustomUser user,
          @RequestParam(defaultValue = "0") int page,
          @RequestParam(defaultValue = "10") int size) {
    Long shopId = user.getShopId();

    PagedResponse<ReservationHistoryResponse> response =
        reservationHistoryQueryService.getReservationHistories(shopId, page, size);

    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
