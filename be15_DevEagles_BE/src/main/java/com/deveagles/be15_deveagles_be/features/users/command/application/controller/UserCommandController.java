package com.deveagles.be15_deveagles_be.features.users.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.shops.command.application.service.ShopCommandService;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.request.ShopAndUserCreateRequest;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.request.ValidCheckRequest;
import com.deveagles.be15_deveagles_be.features.users.command.application.service.UserCommandService;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "회원-직원", description = "회원(직원) 관련 API")
public class UserCommandController {

  private final UserCommandService userCommandService;
  private final ShopCommandService shopCommandService;

  @Transactional
  @PostMapping("/users")
  public ResponseEntity<ApiResponse<Void>> userCreate(
      @RequestBody @Valid ShopAndUserCreateRequest request) {

    Shop shop = shopCommandService.shopRegist(request.shop());
    Staff staff = userCommandService.userRegist(request.user(), shop.getShopId());
    shopCommandService.patchOwnerId(shop, staff.getStaffId());

    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
  }

  @PostMapping("/valid-id")
  public ResponseEntity<ApiResponse<Boolean>> validLoginId(
      @RequestBody @Valid ValidCheckRequest validRequest) {

    Boolean is_valid = userCommandService.validCheckId(validRequest);

    return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(is_valid));
  }

  @PostMapping("/valid-email")
  public ResponseEntity<ApiResponse<Boolean>> validEmail(
      @RequestBody @Valid ValidCheckRequest validRequest) {

    Boolean is_valid = userCommandService.validCheckEmail(validRequest);

    return ResponseEntity.ok().body(ApiResponse.success(is_valid));
  }
}
