package com.deveagles.be15_deveagles_be.features.shops.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.ValidBizNumberRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.response.GetIndustryResponse;
import com.deveagles.be15_deveagles_be.features.shops.command.application.service.ShopCommandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/shop")
@Tag(name = "매장", description = "매장 관련 API")
public class ShopCommandController {

  private final ShopCommandService shopCommandService;

  @PostMapping("/valid-biz")
  public ResponseEntity<ApiResponse<Boolean>> validLoginId(
      @RequestBody @Valid ValidBizNumberRequest validRequest) {

    Boolean is_valid = shopCommandService.validCheckBizNumber(validRequest);

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(is_valid));
  }

  @GetMapping("/get-industry")
  public ResponseEntity<ApiResponse<GetIndustryResponse>> getIndustry() {

    GetIndustryResponse response = shopCommandService.getIndustry();

    return ResponseEntity.ok().body(ApiResponse.success(response));
  }
}
