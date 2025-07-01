package com.deveagles.be15_deveagles_be.features.users.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.messages.command.application.service.MessageSettingsCommandService;
import com.deveagles.be15_deveagles_be.features.shops.command.application.service.ShopCommandService;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.request.*;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.response.AccountResponse;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.response.ProfileResponse;
import com.deveagles.be15_deveagles_be.features.users.command.application.service.UserCommandService;
import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "회원-직원", description = "회원(직원) 관련 API")
public class UserCommandController {

  private final UserCommandService userCommandService;
  private final ShopCommandService shopCommandService;
  private final MessageSettingsCommandService messageSettingsCommandService;

  @Transactional
  @PostMapping()
  public ResponseEntity<ApiResponse<Void>> userCreate(
      @RequestBody @Valid ShopAndUserCreateRequest request) {

    Shop shop = shopCommandService.shopRegist(request.shop());
    Staff staff = userCommandService.userRegist(request.user(), shop.getShopId());
    shopCommandService.patchOwnerId(shop, staff.getStaffId());
    messageSettingsCommandService.createDefault(shop.getShopId());

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

  // todo : Auth 적용
  @PostMapping("/account")
  public ResponseEntity<ApiResponse<AccountResponse>> getAccount(
      @RequestBody @Valid GetAccountRequest accountRequest) {

    AccountResponse response = userCommandService.getAccount(accountRequest);

    return ResponseEntity.ok().body(ApiResponse.success(response));
  }

  @PatchMapping("/account")
  public ResponseEntity<ApiResponse<AccountResponse>> patchAccount(
      @RequestBody @Valid PatchAccountRequest accountRequest) {

    AccountResponse response = userCommandService.patchAccount(accountRequest);

    return ResponseEntity.ok().body(ApiResponse.success(response));
  }

  @GetMapping("/profile")
  public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
      @AuthenticationPrincipal CustomUser customUser) {

    ProfileResponse response = userCommandService.getProfile(customUser);

    return ResponseEntity.ok().body(ApiResponse.success(response));
  }

  @PatchMapping("/profile")
  public ResponseEntity<ApiResponse<ProfileResponse>> patchProfile(
      @AuthenticationPrincipal CustomUser customUser,
      @RequestPart @Valid PatchProfileRequest profileRequest,
      @RequestPart(required = false) MultipartFile profile) {

    ProfileResponse response =
        userCommandService.patchProfile(customUser.getUserId(), profileRequest, profile);

    return ResponseEntity.ok().body(ApiResponse.success(response));
  }
}
