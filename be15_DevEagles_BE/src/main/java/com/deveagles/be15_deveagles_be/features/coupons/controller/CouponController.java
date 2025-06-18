package com.deveagles.be15_deveagles_be.features.coupons.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.coupons.command.dto.CreateCouponCommand;
import com.deveagles.be15_deveagles_be.features.coupons.command.dto.DeleteCouponCommand;
import com.deveagles.be15_deveagles_be.features.coupons.command.service.CouponCommandService;
import com.deveagles.be15_deveagles_be.features.coupons.dto.CouponDto;
import com.deveagles.be15_deveagles_be.features.coupons.query.dto.CouponQuery;
import com.deveagles.be15_deveagles_be.features.coupons.query.dto.CouponResponse;
import com.deveagles.be15_deveagles_be.features.coupons.query.service.CouponQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "쿠폰 관리", description = "쿠폰 생성, 삭제, 조회 API")
@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CouponController {

  private final CouponCommandService couponCommandService;
  private final CouponQueryService couponQueryService;

  @Operation(summary = "쿠폰 생성", description = "새로운 쿠폰을 생성합니다. 쿠폰 코드는 자동으로 생성됩니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "201",
        description = "쿠폰 생성 성공",
        content = @Content(schema = @Schema(implementation = CouponDto.class))),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "잘못된 요청 데이터")
  })
  @PostMapping
  public ResponseEntity<ApiResponse<CouponDto>> createCoupon(
      @Parameter(description = "쿠폰 생성 정보", required = true) @Valid @RequestBody
          CreateCouponCommand command) {
    log.info("쿠폰 생성 요청 - 쿠폰명: {}", command.getCouponTitle());

    CouponDto couponDto = couponCommandService.createCoupon(command);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(couponDto));
  }

  @Operation(summary = "쿠폰 삭제", description = "쿠폰을 소프트 삭제합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "쿠폰 삭제 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "쿠폰을 찾을 수 없음"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "이미 삭제된 쿠폰")
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteCoupon(
      @Parameter(description = "쿠폰 ID", required = true) @PathVariable Long id) {
    log.info("쿠폰 삭제 요청 - ID: {}", id);

    DeleteCouponCommand command = DeleteCouponCommand.builder().id(id).build();
    couponCommandService.deleteCoupon(command);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "쿠폰 상태 토글", description = "쿠폰의 활성화/비활성화 상태를 토글합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "쿠폰 상태 변경 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "쿠폰을 찾을 수 없음"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "삭제된 쿠폰은 상태 변경 불가")
  })
  @PatchMapping("/{id}/toggle")
  public ResponseEntity<ApiResponse<CouponDto>> toggleCouponStatus(
      @Parameter(description = "쿠폰 ID", required = true) @PathVariable Long id) {
    log.info("쿠폰 상태 토글 요청 - ID: {}", id);

    CouponDto couponDto = couponCommandService.toggleCouponStatus(id);
    return ResponseEntity.ok(ApiResponse.success(couponDto));
  }

  @Operation(summary = "쿠폰 ID로 조회", description = "쿠폰 ID를 사용하여 특정 쿠폰을 조회합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "쿠폰 조회 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "쿠폰을 찾을 수 없음")
  })
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CouponResponse>> getCouponById(
      @Parameter(description = "쿠폰 ID", required = true) @PathVariable Long id) {
    log.info("쿠폰 ID로 조회 요청 - ID: {}", id);

    return couponQueryService
        .getCouponById(id)
        .map(coupon -> ResponseEntity.ok(ApiResponse.success(coupon)))
        .orElse(
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure("COUPON_NOT_FOUND", "쿠폰을 찾을 수 없습니다")));
  }

  @Operation(summary = "쿠폰 코드로 조회", description = "쿠폰 코드를 사용하여 특정 쿠폰을 조회합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "쿠폰 조회 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "쿠폰을 찾을 수 없음")
  })
  @GetMapping("/code/{couponCode}")
  public ResponseEntity<ApiResponse<CouponResponse>> getCouponByCode(
      @Parameter(description = "쿠폰 코드", required = true, example = "CP241201ABCD1234") @PathVariable
          String couponCode) {
    log.info("쿠폰 코드로 조회 요청 - 코드: {}", couponCode);

    return couponQueryService
        .getCouponByCode(couponCode)
        .map(coupon -> ResponseEntity.ok(ApiResponse.success(coupon)))
        .orElse(
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure("COUPON_NOT_FOUND", "쿠폰을 찾을 수 없습니다")));
  }

  @Operation(summary = "쿠폰 통합 검색", description = "다양한 조건으로 쿠폰을 검색하고 페이징 결과를 반환합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "쿠폰 검색 성공")
  })
  @GetMapping
  public ResponseEntity<ApiResponse<PagedResponse<CouponResponse>>> searchCoupons(
      @Parameter(description = "쿠폰 코드 (부분 검색)", example = "CP241201")
          @RequestParam(required = false)
          String couponCode,
      @Parameter(description = "쿠폰명 (부분 검색)", example = "할인 쿠폰") @RequestParam(required = false)
          String couponTitle,
      @Parameter(description = "매장 ID", example = "1") @RequestParam(required = false) Long shopId,
      @Parameter(description = "직원 ID", example = "1") @RequestParam(required = false) Long staffId,
      @Parameter(description = "상품 ID", example = "100") @RequestParam(required = false)
          Long primaryItemId,
      @Parameter(description = "활성화 상태 (true: 활성, false: 비활성)", example = "true")
          @RequestParam(required = false)
          Boolean isActive,
      @Parameter(description = "만료일 시작 (YYYY-MM-DD)", example = "2024-01-01")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate expirationDateFrom,
      @Parameter(description = "만료일 종료 (YYYY-MM-DD)", example = "2024-12-31")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate expirationDateTo,
      @Parameter(description = "페이지 번호 (0부터 시작)", example = "0") @RequestParam(defaultValue = "0")
          Integer page,
      @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10")
          Integer size,
      @Parameter(description = "정렬 기준 필드", example = "createdAt")
          @RequestParam(defaultValue = "createdAt")
          String sortBy,
      @Parameter(description = "정렬 방향", example = "desc") @RequestParam(defaultValue = "desc")
          String sortDirection) {

    log.info(
        "쿠폰 통합 검색 요청 - 매장ID: {}, 활성상태: {}, 직원ID: {}, 상품ID: {}",
        shopId,
        isActive,
        staffId,
        primaryItemId);

    CouponQuery query =
        CouponQuery.builder()
            .couponCode(couponCode)
            .couponTitle(couponTitle)
            .shopId(shopId)
            .staffId(staffId)
            .primaryItemId(primaryItemId)
            .isActive(isActive)
            .expirationDateFrom(expirationDateFrom)
            .expirationDateTo(expirationDateTo)
            .page(page)
            .size(size)
            .sortBy(sortBy)
            .sortDirection(sortDirection)
            .build();

    PagedResult<CouponResponse> pagedResult = couponQueryService.searchCoupons(query);
    PagedResponse<CouponResponse> response = PagedResponse.from(pagedResult);

    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
