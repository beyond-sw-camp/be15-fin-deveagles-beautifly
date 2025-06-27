package com.deveagles.be15_deveagles_be.features.membership.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.SessionPassResponse;
import com.deveagles.be15_deveagles_be.features.membership.query.service.SessionPassQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "횟수권 조회", description = "횟수권 조회 API")
@RestController
@RequestMapping("/api/v1/session-pass")
@RequiredArgsConstructor
public class SessionPassQueryController {

  private final SessionPassQueryService sessionPassQueryService;

  @Operation(summary = "횟수권 전체 조회", description = "등록된 모든 횟수권을 조회합니다.")
  @GetMapping
  public ResponseEntity<ApiResponse<List<SessionPassResponse>>> getAllSessionPass() {
    List<SessionPassResponse> response = sessionPassQueryService.getAllSessionPass();
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
