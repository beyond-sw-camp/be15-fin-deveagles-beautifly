package com.deveagles.be15_deveagles_be.features.customers.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.customers.command.application.service.CustomerTagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "고객 태그 관리", description = "고객에게 태그 추가, 제거 API")
@RestController
@RequestMapping("/customers/{customerId}/tags")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CustomerTagController {

  private final CustomerTagService customerTagService;

  @Operation(summary = "고객에게 태그 추가", description = "특정 고객에게 태그를 추가합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "태그 추가 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "고객 또는 태그를 찾을 수 없음"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "이미 할당된 태그")
  })
  @PostMapping("/{tagId}")
  public ResponseEntity<ApiResponse<Void>> addTagToCustomer(
      @Parameter(description = "고객 ID", required = true, example = "1") @PathVariable
          Long customerId,
      @Parameter(description = "태그 ID", required = true, example = "1") @PathVariable Long tagId,
      @Parameter(description = "매장 ID", required = true, example = "1") @RequestParam Long shopId) {
    log.info("고객 태그 추가 요청 - 고객ID: {}, 태그ID: {}, 매장ID: {}", customerId, tagId, shopId);

    customerTagService.addTagToCustomer(customerId, tagId, shopId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "고객에서 태그 제거", description = "특정 고객에서 태그를 제거합니다.")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "태그 제거 성공"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "고객을 찾을 수 없거나 할당되지 않은 태그")
  })
  @DeleteMapping("/{tagId}")
  public ResponseEntity<ApiResponse<Void>> removeTagFromCustomer(
      @Parameter(description = "고객 ID", required = true, example = "1") @PathVariable
          Long customerId,
      @Parameter(description = "태그 ID", required = true, example = "1") @PathVariable Long tagId,
      @Parameter(description = "매장 ID", required = true, example = "1") @RequestParam Long shopId) {
    log.info("고객 태그 제거 요청 - 고객ID: {}, 태그ID: {}, 매장ID: {}", customerId, tagId, shopId);

    customerTagService.removeTagFromCustomer(customerId, tagId, shopId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
