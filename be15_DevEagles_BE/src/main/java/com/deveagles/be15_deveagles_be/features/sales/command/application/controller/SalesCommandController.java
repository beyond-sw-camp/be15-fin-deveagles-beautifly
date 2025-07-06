package com.deveagles.be15_deveagles_be.features.sales.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.sales.command.application.service.SalesCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "매출", description = "매출 API")
@RestController
@RequestMapping("sales")
@RequiredArgsConstructor
@Validated
@Slf4j
public class SalesCommandController {

  private final SalesCommandService salesCommandService;

  @Operation(summary = "회원권 매출 환불", description = "해당 회원권 salesId의 매출을 환불 처리합니다.")
  @PostMapping("/refund/{salesId}")
  public ResponseEntity<ApiResponse<Void>> refundSales(@PathVariable Long salesId) {
    salesCommandService.refundSales(salesId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "매출 삭제", description = "해당 salesId의 매출을 soft delete 처리합니다.")
  @DeleteMapping("/delete/{salesId}")
  public ResponseEntity<ApiResponse<Void>> deleteSales(@PathVariable Long salesId) {
    salesCommandService.deleteSales(salesId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
