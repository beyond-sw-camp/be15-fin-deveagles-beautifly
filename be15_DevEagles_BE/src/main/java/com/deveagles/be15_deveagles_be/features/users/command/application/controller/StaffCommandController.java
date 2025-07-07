package com.deveagles.be15_deveagles_be.features.users.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.request.CreateStaffRequest;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.response.StaffInfoResponse;
import com.deveagles.be15_deveagles_be.features.users.command.application.service.StaffCommandService;
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
@RequestMapping("/staffs")
@Tag(name = "직원 관리", description = "직원 관리 관련 API")
public class StaffCommandController {

  private final StaffCommandService staffCommandService;

  @Transactional
  @PostMapping()
  public ResponseEntity<ApiResponse<Void>> staffCreate(
      @AuthenticationPrincipal CustomUser customUser,
      @RequestPart @Valid CreateStaffRequest staffRequest,
      @RequestPart(required = false) MultipartFile profile) {

    staffCommandService.staffCreate(customUser.getShopId(), staffRequest, profile);

    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
  }

  @Transactional
  @GetMapping("/{staffId}")
  public ResponseEntity<ApiResponse<StaffInfoResponse>> getStaffDetail(
      @AuthenticationPrincipal CustomUser customUser, @PathVariable Long staffId) {

    StaffInfoResponse response = staffCommandService.getStaffDetail(staffId);

    return ResponseEntity.ok().body(ApiResponse.success(response));
  }
}
