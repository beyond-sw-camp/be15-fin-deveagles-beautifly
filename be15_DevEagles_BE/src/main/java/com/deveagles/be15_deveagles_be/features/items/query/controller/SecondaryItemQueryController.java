package com.deveagles.be15_deveagles_be.features.items.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.items.query.dto.response.SecondaryItemResponse;
import com.deveagles.be15_deveagles_be.features.items.query.service.SecondaryItemQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "2차 상품 조회", description = "2차 상품 조회 API")
@RestController
@RequestMapping("/api/v1/secondary-items")
@RequiredArgsConstructor
public class SecondaryItemQueryController {

  private final SecondaryItemQueryService secondaryItemQueryService;

  @Operation(summary = "2차 상품 전체 조회", description = "등록된 모든 2차 상품을 조회합니다.")
  @GetMapping
  public ResponseEntity<ApiResponse<List<SecondaryItemResponse>>> getAllSecondaryItems() {
    List<SecondaryItemResponse> response = secondaryItemQueryService.getAllSecondaryItems();
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
