package com.deveagles.be15_deveagles_be.features.items.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.items.command.application.dto.request.SecondaryItemRegistRequest;
import com.deveagles.be15_deveagles_be.features.items.command.application.dto.request.SecondaryItemUpdateRequest;
import com.deveagles.be15_deveagles_be.features.items.command.application.service.SecondaryItemCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "2차 상품", description = "2차 상품 API")
@RestController
@RequestMapping("/api/v1/secondary-items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class SecondaryItemCommandController {

  private final SecondaryItemCommandService secondaryItemCommandService;

  @Operation(summary = "2차 상품 등록", description = "2차 상품을 등록합니다.")
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> registerSecondaryItem(
      @RequestBody SecondaryItemRegistRequest request) {

    secondaryItemCommandService.registerSecondaryItem(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "2차 상품 수정", description = "2차 상품을 수정합니다.")
  @PutMapping("/{secondaryItemId}")
  public ResponseEntity<ApiResponse<Void>> updatePrimaryItem(
      @PathVariable Long secondaryItemId, @RequestBody SecondaryItemUpdateRequest request) {
    secondaryItemCommandService.updateSecondaryItem(secondaryItemId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
