package com.deveagles.be15_deveagles_be.features.sales.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.sales.command.application.dto.request.PrepaidPassSalesRequest;
import com.deveagles.be15_deveagles_be.features.sales.command.application.service.PrepaidPassSalesCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "선불권 매출", description = "선불권 매출 API")
@RestController
@RequestMapping("prepaid-pass-sales")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrepaidPassSalesCommandController {

  private final PrepaidPassSalesCommandService prepaidPassSalesCommandService;

  @Operation(summary = "선불권 매출 등록", description = "선불권을 매출을 등록합니다.")
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> registerPrepaidPassSales(
      @Validated @RequestBody PrepaidPassSalesRequest request) {
    prepaidPassSalesCommandService.registPrepaidPassSales(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
