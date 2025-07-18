package com.deveagles.be15_deveagles_be.features.items.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.items.command.application.dto.request.PrimaryItemRequest;
import com.deveagles.be15_deveagles_be.features.items.command.application.service.PrimaryItemCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "1차 상품", description = "1차 상품 API")
@RestController
@RequestMapping("/primary-items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrimaryItemCommandController {

  private final PrimaryItemCommandService primaryItemServiceCommand;

  @Operation(summary = "1차 상품 등록", description = "1차 상품을 등록합니다.")
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> registerPrimaryItem(
      @RequestBody PrimaryItemRequest request) {
    primaryItemServiceCommand.registerPrimaryItem(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "1차 상품 수정", description = "1차 상품을 수정합니다.")
  @PutMapping("/{primaryItemId}")
  public ResponseEntity<ApiResponse<Void>> updatePrimaryItem(
      @PathVariable Long primaryItemId, @RequestBody PrimaryItemRequest request) {
    primaryItemServiceCommand.updatePrimaryItem(primaryItemId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "1차 상품 삭제", description = "1차 상품을 soft delete 합니다.")
  @DeleteMapping("/{primaryItemId}")
  public ResponseEntity<ApiResponse<Void>> deletePrimaryItem(@PathVariable Long primaryItemId) {
    primaryItemServiceCommand.deletePrimaryItem(primaryItemId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
