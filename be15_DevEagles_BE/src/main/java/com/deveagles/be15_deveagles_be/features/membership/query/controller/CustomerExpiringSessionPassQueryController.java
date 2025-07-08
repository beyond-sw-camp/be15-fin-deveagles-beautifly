package com.deveagles.be15_deveagles_be.features.membership.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.request.CustomerExpiringSessionPassFilterRequest;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.CustomerExpiringSessionPassResult;
import com.deveagles.be15_deveagles_be.features.membership.query.service.CustomerExpiringSessionPassQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "만료 예정 횟수권 조회", description = "만료 예정인 고객의 횟수권 정보 조회 API")
@RestController
@RequestMapping("/customer-expiring-session-passes")
@RequiredArgsConstructor
public class CustomerExpiringSessionPassQueryController {

  private final CustomerExpiringSessionPassQueryService customerExpiringSessionPassQueryService;

  @Operation(summary = "만료 예정 횟수권 조회", description = "조건(잔여 횟수, 만료일 등)에 따라 만료 예정 횟수권을 조회합니다.")
  @GetMapping
  public ResponseEntity<ApiResponse<CustomerExpiringSessionPassResult>> getExpiringSessionPasses(
      @AuthenticationPrincipal CustomUser user,
      @RequestParam(required = false) Integer minRemainingCount,
      @RequestParam(required = false) Integer maxRemainingCount,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {
    CustomerExpiringSessionPassFilterRequest filter =
        new CustomerExpiringSessionPassFilterRequest();
    filter.setMinRemainingCount(minRemainingCount);
    filter.setMaxRemainingCount(maxRemainingCount);
    filter.setStartDate(startDate);
    filter.setEndDate(endDate);
    filter.setPage(page);
    filter.setSize(size);

    CustomerExpiringSessionPassResult result =
        customerExpiringSessionPassQueryService.getExpiringSessionPasses(user.getShopId(), filter);

    return ResponseEntity.ok(ApiResponse.success(result));
  }
}
