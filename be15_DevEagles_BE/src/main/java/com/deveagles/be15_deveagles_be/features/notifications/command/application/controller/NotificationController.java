package com.deveagles.be15_deveagles_be.features.notifications.command.application.controller;

import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.notifications.command.application.service.NotificationSseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "알림 실시간 API", description = "SSE를 이용한 실시간 알림 구독 API")
@RestController
@RequestMapping("/notifications") // 기존 조회 컨트롤러와 경로가 겹치지 않도록 주의
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationSseService notificationSseService;

  @Operation(summary = "알림 구독", description = "로그인한 사용자가 실시간 알림을 구독합니다. (SSE)")
  @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal CustomUser customUser) {

    // 로그인한 사용자의 shopId를 사용하여 구독을 시작합니다.
    SseEmitter emitter = notificationSseService.subscribe(customUser.getShopId());
    return ResponseEntity.ok(emitter);
  }
}
