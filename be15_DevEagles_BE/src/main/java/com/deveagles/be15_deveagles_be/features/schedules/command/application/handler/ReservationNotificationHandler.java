package com.deveagles.be15_deveagles_be.features.schedules.command.application.handler;

import com.deveagles.be15_deveagles_be.common.events.ReservationCreatedEvent;
import com.deveagles.be15_deveagles_be.features.notifications.command.application.dto.CreateNotificationRequest;
import com.deveagles.be15_deveagles_be.features.notifications.command.application.service.NotificationCommandService;
import com.deveagles.be15_deveagles_be.features.notifications.command.application.service.NotificationSseService;
import com.deveagles.be15_deveagles_be.features.notifications.command.domain.aggregate.NotificationType;
import com.deveagles.be15_deveagles_be.features.notifications.query.application.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationNotificationHandler {

  private final NotificationCommandService notificationCommandService;
  private final NotificationSseService notificationSseService;

  // @EventListener 를 @TransactionalEventListener 로 변경하여
  // 예약 정보가 DB에 100% 안전하게 저장된 후에만 아래 코드를 실행하도록 보장합니다.
  @TransactionalEventListener
  public void handle(ReservationCreatedEvent event) {
    log.info("[Event] ReservationCreatedEvent 수신 - shopId: {}", event.shopId());

    // 1. 알림 생성에 필요한 데이터를 가공합니다.
    CreateNotificationRequest request =
        new CreateNotificationRequest(
            event.shopId(),
            NotificationType.RESERVATION,
            "새로운 예약 신청",
            String.format("고객 '%s'님이 예약을 신청했습니다. 확인해주세요.", event.customerName()));

    // 2. 알림을 DB에 저장하고, 저장된 알림 정보를 반환받습니다.
    NotificationResponse savedNotification = notificationCommandService.create(request);

    // 3. ✨ [핵심 해결책] 저장된 알림 정보를 클라이언트에게 실시간으로 발송합니다.
    // 이 코드가 없으면 알림은 DB에만 저장되고 프론트엔드로는 절대 전송되지 않습니다.
    if (savedNotification != null) {
      notificationSseService.send(event.shopId(), savedNotification);
    }
  }
}
