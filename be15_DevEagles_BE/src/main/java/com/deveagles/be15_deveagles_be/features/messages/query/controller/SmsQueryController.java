package com.deveagles.be15_deveagles_be.features.messages.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.messages.query.dto.response.SmsDetailResponse;
import com.deveagles.be15_deveagles_be.features.messages.query.dto.response.SmsListResponse;
import com.deveagles.be15_deveagles_be.features.messages.query.service.SmsQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class SmsQueryController {

  private final SmsQueryService smsQueryService;

  @GetMapping
  public ResponseEntity<ApiResponse<PagedResult<SmsListResponse>>> getSmsList(
      @AuthenticationPrincipal CustomUser customUser,
      @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
          Pageable pageable) {

    System.out.println(customUser.getShopId());
    PagedResult<SmsListResponse> result =
        smsQueryService.getSmsList(customUser.getShopId(), pageable);
    System.out.println("📦 [백엔드 응답 디버깅]");
    System.out.println("→ totalItems = " + result.getPagination().getTotalItems());
    System.out.println("→ totalPages = " + result.getPagination().getTotalPages());
    System.out.println("→ currentPage = " + result.getPagination().getCurrentPage());
    System.out.println("→ content.size() = " + result.getContent().size());
    return ResponseEntity.ok(ApiResponse.success(result));
  }

  @GetMapping("/{messageId}")
  public ResponseEntity<ApiResponse<SmsDetailResponse>> getSmsDetail(
      @AuthenticationPrincipal CustomUser customUser, @PathVariable Long messageId) {
    SmsDetailResponse response = smsQueryService.getSmsDetail(customUser.getShopId(), messageId);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
