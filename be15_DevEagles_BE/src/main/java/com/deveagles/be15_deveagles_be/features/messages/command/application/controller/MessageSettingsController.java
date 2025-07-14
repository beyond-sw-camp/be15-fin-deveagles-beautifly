package com.deveagles.be15_deveagles_be.features.messages.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.MessageSettingRequest;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.response.MessageSettingResponse;
import com.deveagles.be15_deveagles_be.features.messages.command.application.service.MessageSettingsCommandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message/settings")
@RequiredArgsConstructor
public class MessageSettingsController {
  private final MessageSettingsCommandService messageSettingsCommandService;

  @PostMapping()
  public ResponseEntity<ApiResponse<Long>> create(@PathVariable Long shopId) {
    Long id = messageSettingsCommandService.createDefault(shopId);
    return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(shopId));
  }

  @GetMapping()
  public ResponseEntity<ApiResponse<MessageSettingResponse>> getSettings(
      @AuthenticationPrincipal CustomUser customUser) {
    Long shopId = customUser.getShopId();
    return ResponseEntity.ok(
        ApiResponse.success(messageSettingsCommandService.loadSettings(shopId)));
  }

  @PutMapping()
  public ResponseEntity<ApiResponse<Void>> update(
      @AuthenticationPrincipal CustomUser customUser,
      @Valid @RequestBody MessageSettingRequest request) {
    Long shopId = customUser.getShopId();
    messageSettingsCommandService.updateSettings(shopId, request);
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
