package com.deveagles.be15_deveagles_be.features.membership.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.PrepaidPassResponse;
import com.deveagles.be15_deveagles_be.features.membership.query.service.PrepaidPassQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "선불권 조회", description = "선불권 조회 API")
@RestController
@RequestMapping("/prepaid-pass")
@RequiredArgsConstructor
public class PrepaidPassQueryController {

  private final PrepaidPassQueryService prepaidPassQueryService;

  @Operation(summary = "선불권 전체 조회", description = "등록된 모든 선불권을 조회합니다.")
  @GetMapping
  public ResponseEntity<ApiResponse<List<PrepaidPassResponse>>> getAllPrepaidPass() {
    List<PrepaidPassResponse> response = prepaidPassQueryService.getAllPrepaidPass();
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
