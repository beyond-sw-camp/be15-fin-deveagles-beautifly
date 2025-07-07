package com.deveagles.be15_deveagles_be.features.membership.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.PrepaidPassRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.application.service.PrepaidPassCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "선불권", description = "선불권 API")
@RestController
@RequestMapping("/prepaid-pass")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrepaidPassCommandController {

  private final PrepaidPassCommandService prepaidPassCommandService;

  @Operation(summary = "선불권 등록", description = "선불권을 등록합니다.")
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> registerPrepaidPass(
      @RequestBody PrepaidPassRequest request) {
    prepaidPassCommandService.registPrepaidPass(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "선불권 수정", description = "선불권을 수정합니다.")
  @PutMapping("/{prepaidPassId}")
  public ResponseEntity<ApiResponse<Void>> updatePrepaidPass(
      @PathVariable Long prepaidPassId, @RequestBody PrepaidPassRequest request) {
    prepaidPassCommandService.updatePrepaidPass(prepaidPassId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "선불권 삭제", description = "선불권을 soft delete 합니다.")
  @DeleteMapping("/{prepaidPassId}")
  public ResponseEntity<ApiResponse<Void>> deletePrepaidPass(@PathVariable Long prepaidPassId) {
    prepaidPassCommandService.deletePrepaidPass(prepaidPassId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
