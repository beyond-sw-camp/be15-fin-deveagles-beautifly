package com.deveagles.be15_deveagles_be.features.membership.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.request.CustomerMemebershipFilterRequest;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.CustomerMembershipResult;
import com.deveagles.be15_deveagles_be.features.membership.query.service.CustomerMembershipQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "회원권 조회", description = "고객 회원권 관련 조회 API")
@RestController
@RequestMapping("/customer-memberships")
@RequiredArgsConstructor
public class CustomerMembershipQueryController {

  private final CustomerMembershipQueryService customerMembershipQueryService;

  @Operation(summary = "고객 회원권 전체 조회", description = "샵에 등록된 모든 고객의 회원권 정보를 조회합니다.")
  @GetMapping
  public ResponseEntity<ApiResponse<CustomerMembershipResult>> getAllCustomerMemberships(
      @AuthenticationPrincipal CustomUser user,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "10") int size) {

    CustomerMembershipResult result =
        customerMembershipQueryService.getCustomerMembershipList(user.getShopId(), page, size);
    return ResponseEntity.ok(ApiResponse.success(result));
  }

  @Operation(summary = "고객 회원권 필터 조회", description = "잔여 선불권 금액 등 조건에 따라 고객 회원권 정보를 조회합니다.")
  @PostMapping("/filter")
  public ResponseEntity<ApiResponse<CustomerMembershipResult>> getFilteredCustomerMemberships(
      @AuthenticationPrincipal CustomUser user,
      @RequestBody @Valid CustomerMemebershipFilterRequest filter) {

    CustomerMembershipResult result =
        customerMembershipQueryService.getCustomerMembershipList(user.getShopId(), filter);
    return ResponseEntity.ok(ApiResponse.success(result));
  }
}
