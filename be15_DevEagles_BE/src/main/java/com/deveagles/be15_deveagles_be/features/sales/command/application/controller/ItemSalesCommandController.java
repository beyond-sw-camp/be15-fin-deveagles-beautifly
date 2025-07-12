package com.deveagles.be15_deveagles_be.features.sales.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.sales.command.application.dto.request.ItemSalesRequest;
import com.deveagles.be15_deveagles_be.features.sales.command.application.service.ItemSalesCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "상품 매출", description = "상품 매출 API")
@RestController
@RequestMapping("item-sales")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemSalesCommandController {

  private final ItemSalesCommandService itemSalesCommandService;

  @Operation(summary = "상품 매출 등록", description = "상품 매출을 등록합니다.")
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> registerItemSales(
      @AuthenticationPrincipal CustomUser user, @Validated @RequestBody ItemSalesRequest request) {
    request.setShopId(user.getShopId());
    itemSalesCommandService.registItemSales(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "상품 매출 수정", description = "기존 상품 매출을 수정합니다.")
  @PutMapping("/{salesId}")
  public ResponseEntity<ApiResponse<Void>> updateItemSales(
      @AuthenticationPrincipal CustomUser user,
      @PathVariable Long salesId,
      @Validated @RequestBody ItemSalesRequest request) {

    request.setShopId(user.getShopId());
    itemSalesCommandService.updateItemSales(salesId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "상품 매출 환불", description = "기존 상품 매출을 환불 처리합니다.")
  @PutMapping("/refund/{salesId}")
  public ResponseEntity<ApiResponse<Void>> refundItemSales(
      @AuthenticationPrincipal CustomUser user, @PathVariable Long salesId) {

    itemSalesCommandService.refundItemSales(salesId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
