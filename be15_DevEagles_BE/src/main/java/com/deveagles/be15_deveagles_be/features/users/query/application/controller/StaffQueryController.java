package com.deveagles.be15_deveagles_be.features.users.query.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.users.query.application.dto.request.GetStaffsListRequest;
import com.deveagles.be15_deveagles_be.features.users.query.application.dto.response.StaffsListResponse;
import com.deveagles.be15_deveagles_be.features.users.query.application.service.StaffQueryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/staffs")
@Tag(name = "직원 관리", description = "직원 관리 관련 API")
public class StaffQueryController {

  private final StaffQueryService staffQueryService;

  @GetMapping()
  public ResponseEntity<ApiResponse<StaffsListResponse>> getStaff(
      @AuthenticationPrincipal CustomUser customUser,
      @ModelAttribute GetStaffsListRequest request) {

    StaffsListResponse response =
        staffQueryService.getStaff(
            customUser.getShopId(),
            request.size(),
            request.page(),
            request.keyword(),
            request.isActive());

    return ResponseEntity.ok().body(ApiResponse.success(response));
  }
}
