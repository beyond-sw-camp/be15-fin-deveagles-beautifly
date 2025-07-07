package com.deveagles.be15_deveagles_be.features.membership.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.SessionPassRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.application.service.SessionPassCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "횟수권", description = "횟수권 API")
@RestController
@RequestMapping("/session-pass")
@RequiredArgsConstructor
@Validated
@Slf4j
public class SessionPassCommandController {

  private final SessionPassCommandService sessionPassCommandService;

  @Operation(summary = "횟수권 등록", description = "횟수권을 등록합니다.")
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> registerSessionPass(
      @RequestBody SessionPassRequest request) {
    sessionPassCommandService.registSessionPass(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "횟수권 수정", description = "횟수권을 수정합니다.")
  @PutMapping("/{sessionPassId}")
  public ResponseEntity<ApiResponse<Void>> updateSessionPass(
      @PathVariable Long sessionPassId, @RequestBody SessionPassRequest request) {
    sessionPassCommandService.updateSessionPass(sessionPassId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @Operation(summary = "횟수권 삭제", description = "횟수권을 soft delete 합니다.")
  @DeleteMapping("/{sessionPassId}")
  public ResponseEntity<ApiResponse<Void>> deleteSessionPass(@PathVariable Long sessionPassId) {
    sessionPassCommandService.deleteSessionPass(sessionPassId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
