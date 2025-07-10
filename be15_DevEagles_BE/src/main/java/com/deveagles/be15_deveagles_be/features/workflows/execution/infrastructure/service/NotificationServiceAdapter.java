package com.deveagles.be15_deveagles_be.features.workflows.execution.infrastructure.service;

import com.deveagles.be15_deveagles_be.features.notifications.command.application.dto.CreateNotificationRequest;
import com.deveagles.be15_deveagles_be.features.notifications.command.application.service.NotificationCommandService;
import com.deveagles.be15_deveagles_be.features.notifications.command.domain.aggregate.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceAdapter {

  private final NotificationCommandService notificationCommandService;

  public boolean sendNotification(
      Long shopId, Long staffId, String title, String content, String level, int targetCount) {
    try {
      NotificationType type;
      if ("ANALYSIS".equalsIgnoreCase(level)) {
        type = NotificationType.ANALYSIS;
      } else if ("RESERVATION".equalsIgnoreCase(level)) {
        type = NotificationType.RESERVATION;
      } else {
        type = NotificationType.NOTICE;
      }

      CreateNotificationRequest request =
          new CreateNotificationRequest(shopId, type, title, content);
      notificationCommandService.create(request);
      return true;
    } catch (Exception e) {
      log.error("시스템 알림 저장 실패", e);
      return false;
    }
  }
}
