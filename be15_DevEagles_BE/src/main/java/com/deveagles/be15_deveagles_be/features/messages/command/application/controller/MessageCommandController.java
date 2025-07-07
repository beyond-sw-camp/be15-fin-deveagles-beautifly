package com.deveagles.be15_deveagles_be.features.messages.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.SmsRequest;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.UpdateReservationRequest;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.response.MessageSendResult;
import com.deveagles.be15_deveagles_be.features.messages.command.application.service.MessageCommandService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageCommandController {

  private final MessageCommandService messageCommandService;

  @PostMapping("/send")
  public ResponseEntity<ApiResponse<List<MessageSendResult>>> sendSms(
      @AuthenticationPrincipal CustomUser customUser, @RequestBody @Valid SmsRequest request) {

    List<MessageSendResult> resultList =
        messageCommandService.sendSms(customUser.getShopId(), request);
    return ResponseEntity.ok(ApiResponse.success(resultList));
  }

  @PutMapping("/{messageId}")
  public ResponseEntity<ApiResponse<Void>> updateReservation(
      @AuthenticationPrincipal CustomUser customUser,
      @RequestBody @Valid UpdateReservationRequest updateReservationRequest,
      @PathVariable Long messageId) {
    messageCommandService.updateReservationMessage(
        updateReservationRequest, customUser.getShopId(), messageId);
    return ResponseEntity.ok(ApiResponse.success(null));
  }

  @DeleteMapping("/{messageId}/cancel")
  public ResponseEntity<ApiResponse<Void>> cancelReservation(
      @AuthenticationPrincipal CustomUser customUser, @PathVariable Long messageId) {
    messageCommandService.cancelScheduledMessage(messageId, customUser.getShopId());

    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
