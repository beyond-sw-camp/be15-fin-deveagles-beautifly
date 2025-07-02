package com.deveagles.be15_deveagles_be.features.messages.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.SmsRequest;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.response.SmsResponse;
import com.deveagles.be15_deveagles_be.features.messages.command.application.service.MessageCommandService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/message")
public class MessageCommandController {

  private final MessageCommandService messageCommandService;

  @PostMapping
  public ResponseEntity<ApiResponse<List<SmsResponse>>> sendSms(
      @RequestBody @Valid SmsRequest request) {
    List<SmsResponse> response = messageCommandService.sendSms(request);
    return ResponseEntity.ok(ApiResponse.success(response));
  }
}
