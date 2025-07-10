package com.deveagles.be15_deveagles_be.features.membership.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.CustomerPrepaidPassUpdateRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.application.service.CustomerPrepaidPassCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "고객 선불권", description = "고객 선불권 관련 API")
@RestController
@RequestMapping("/customer-prepaid-pass")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CustomerPrepaidPassCommandController {

  private final CustomerPrepaidPassCommandService customerPrepaidPassCommandService;

  @Operation(summary = "고객 선불권 수정", description = "고객의 선불권 정보를 수정합니다.")
  @PutMapping
  public ResponseEntity<ApiResponse<Void>> updateCustomerPrepaidPass(
      @RequestBody @Valid CustomerPrepaidPassUpdateRequest request) {

    customerPrepaidPassCommandService.updateCustomerPrepaidPass(request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
