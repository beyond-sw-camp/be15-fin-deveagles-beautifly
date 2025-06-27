package com.deveagles.be15_deveagles_be.features.membership.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.SessionPassRegistRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.application.service.SessionPassCommandService;
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

@Tag(name = "횟수권", description = "횟수권 API")
@RestController
@RequestMapping("/api/v1/session-pass")
@RequiredArgsConstructor
@Validated
@Slf4j
public class SessionPassCommandController {

  private final SessionPassCommandService sessionPassCommandService;

  @Operation(summary = "횟수권 등록", description = "횟수권을 등록합니다.")
  @PostMapping
  public ResponseEntity<ApiResponse<Void>> registerSessionPass(
      @RequestBody SessionPassRegistRequest request) {
    sessionPassCommandService.registSessionPass(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
