package com.deveagles.be15_deveagles_be.features.notifications.command.application.controller;

import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.notifications.command.application.service.NotificationCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "알림 변경 API", description = "알림 상태 변경 관련 API")
@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationCommandController {

  private final NotificationCommandService notificationCommandService;

  @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
  @PatchMapping("/{notificationId}/read") // 예: PATCH /api/v1/notifications/101/read
  public ResponseEntity<Void> markNotificationAsRead(
      @AuthenticationPrincipal CustomUser customUser, @PathVariable Long notificationId) {

    notificationCommandService.markAsRead(customUser.getShopId(), notificationId);

    return ResponseEntity.noContent().build();
  }
}
