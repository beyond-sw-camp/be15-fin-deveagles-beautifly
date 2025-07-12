package com.deveagles.be15_deveagles_be.features.notifications.command.application.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.deveagles.be15_deveagles_be.features.notifications.command.domain.aggregate.NotificationType;
import com.deveagles.be15_deveagles_be.features.notifications.query.application.dto.NotificationResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@ExtendWith(MockitoExtension.class)
class NotificationSseServiceTest {

  @InjectMocks private NotificationSseService notificationSseService;

  // SseEmitter는 실제 객체를 사용하되, 특정 메서드 호출을 감시하기 위해 @Spy 사용
  @Spy private SseEmitter mockEmitter = new SseEmitter();

  @Test
  @DisplayName("SSE 구독 성공 테스트")
  void subscribe_success() {
    // given
    final Long shopId = 1L;

    // when
    SseEmitter emitter = notificationSseService.subscribe(shopId);

    // then
    assertThat(emitter).isNotNull();
  }

  @Test
  @DisplayName("알림 발송 성공 테스트")
  void send_notification_success() throws IOException {
    // given
    final Long shopId = 1L;
    // 구독자가 있는 상황을 가정하여 emitters 맵에 직접 추가
    notificationSseService.subscribe(shopId);

    NotificationResponse notification =
        new NotificationResponse(
            101L, "테스트 알림", "내용입니다.", NotificationType.RESERVATION, false, LocalDateTime.now());

    // when
    notificationSseService.send(shopId, notification);
  }

  @Test
  @DisplayName("구독하지 않은 사용자에게는 알림이 발송되지 않음")
  void send_notification_fail_when_not_subscribed() throws IOException {
    // given
    final Long subscribedShopId = 1L;
    final Long unsubscribedShopId = 2L;

    // 1번 매장만 구독
    notificationSseService.subscribe(subscribedShopId);

    NotificationResponse notification =
        new NotificationResponse(
            102L,
            "테스트 알림",
            "구독 안한 사람에게 가는 내용",
            NotificationType.NOTICE,
            false,
            LocalDateTime.now());

    // when
    // 구독하지 않은 2번 매장에게 알림 발송 시도
    notificationSseService.send(unsubscribedShopId, notification);
  }
}
