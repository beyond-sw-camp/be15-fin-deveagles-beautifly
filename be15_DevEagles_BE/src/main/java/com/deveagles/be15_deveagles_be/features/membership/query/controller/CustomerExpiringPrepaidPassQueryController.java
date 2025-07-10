package com.deveagles.be15_deveagles_be.features.membership.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.request.CustomerExpiringPrepaidPassFilterRequest;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.CustomerExpiringPrepaidPassResult;
import com.deveagles.be15_deveagles_be.features.membership.query.service.CustomerExpiringPrepaidPassQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "만료 예정 선불권 조회", description = "만료 예정인 고객의 선불권 정보 조회 API")
@RestController
@RequestMapping("/customer-expiring-prepaid-passes")
@RequiredArgsConstructor
public class CustomerExpiringPrepaidPassQueryController {

  private final CustomerExpiringPrepaidPassQueryService customerExpiringPrepaidPassQueryService;

  @Operation(summary = "만료 예정 선불권 조회", description = "조건(잔여 횟수, 만료일 등)에 따라 만료 예정 선불권을 조회합니다.")
  @GetMapping
  public ResponseEntity<ApiResponse<CustomerExpiringPrepaidPassResult>> getExpiringPrepaidPasses(
      @AuthenticationPrincipal CustomUser user,
      @ModelAttribute CustomerExpiringPrepaidPassFilterRequest filter) {

    filter.setPage(Math.max(filter.getPage(), 1)); // 기본값 보장
    filter.setSize(Math.max(filter.getSize(), 10)); // 기본값 보장

    CustomerExpiringPrepaidPassResult result =
        customerExpiringPrepaidPassQueryService.getExpiringPrepaidPasses(user.getShopId(), filter);

    return ResponseEntity.ok(ApiResponse.success(result));
  }
}
