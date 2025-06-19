package com.deveagles.be15_deveagles_be.features.coupons.presentation.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.coupons.application.validation.CouponApplicationService;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.request.CouponApplicationRequest;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response.CouponApplicationResponse;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response.CouponValidationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "쿠폰 검증/적용", description = "쿠폰 유효성 검증 및 할인 적용 API")
@RestController
@RequestMapping("/coupons/validation")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CouponValidationController {

  private final CouponApplicationService couponApplicationService;

  @Operation(summary = "쿠폰 유효성 검증", description = "매출 등록 전 쿠폰의 유효성을 검증합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "쿠폰 검증 완료 (성공/실패 여부는 응답 데이터 확인)"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 데이터")
  })
  @PostMapping("/validate")
  public ResponseEntity<ApiResponse<CouponValidationResponse>> validateCoupon(
      @Parameter(description = "쿠폰 검증 요청 정보", required = true) @Valid @RequestBody
          CouponApplicationRequest request) {
    log.info("쿠폰 검증 요청 - 코드: {}, 매장ID: {}", request.getCouponCode(), request.getShopId());

    CouponValidationResponse result = couponApplicationService.validateCoupon(request);
    return ResponseEntity.ok(ApiResponse.success(result));
  }

  @Operation(summary = "쿠폰 적용 시뮬레이션", description = "실제 적용하지 않고 쿠폰 적용 시 할인 금액을 계산합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "쿠폰 적용 시뮬레이션 완료"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 데이터")
  })
  @PostMapping("/apply-simulation")
  public ResponseEntity<ApiResponse<CouponApplicationResponse>> applyCouponSimulation(
      @Parameter(description = "쿠폰 적용 요청 정보", required = true) @Valid @RequestBody
          CouponApplicationRequest request) {
    log.info(
        "쿠폰 적용 시뮬레이션 요청 - 코드: {}, 원금액: {}", request.getCouponCode(), request.getOriginalAmount());

    CouponApplicationResponse result = couponApplicationService.applyCoupon(request);
    return ResponseEntity.ok(ApiResponse.success(result));
  }
}
