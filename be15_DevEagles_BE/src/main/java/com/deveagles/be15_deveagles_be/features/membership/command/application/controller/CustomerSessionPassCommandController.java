package com.deveagles.be15_deveagles_be.features.membership.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerSessionPassUpdateRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.application.service.CustomerSessionPassCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "고객 횟수권", description = "고객 횟수권 관련 API")
@RestController
@RequestMapping("/customer-session-pass")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CustomerSessionPassCommandController {

  private final CustomerSessionPassCommandService customerSessionPassCommandService;

  @Operation(summary = "고객 선불권 수정", description = "고객의 선불권 정보를 수정합니다.")
  @PutMapping
  public ResponseEntity<ApiResponse<Void>> updateCustomerSessionPass(
      @RequestBody @Valid CustomerSessionPassUpdateRequest request) {

    customerSessionPassCommandService.updateCustomerSessionPass(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
