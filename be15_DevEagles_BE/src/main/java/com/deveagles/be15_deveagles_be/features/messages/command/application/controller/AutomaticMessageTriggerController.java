package com.deveagles.be15_deveagles_be.features.messages.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.AutomaticCreateRequest;
import com.deveagles.be15_deveagles_be.features.messages.command.application.service.AutomaticMessageTriggerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message/auto-message/{shopId}")
public class AutomaticMessageTriggerController {
  private final AutomaticMessageTriggerService automaticMessageTriggerService;

  @PostMapping
  public ResponseEntity<ApiResponse<Void>> registerAutoMessage(
      @AuthenticationPrincipal CustomUser customUser,
      @RequestBody @Valid AutomaticCreateRequest automaticCreateRequest) {
    automaticMessageTriggerService.registerAutomaticMessage(
        customUser.getShopId(), automaticCreateRequest);

    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
