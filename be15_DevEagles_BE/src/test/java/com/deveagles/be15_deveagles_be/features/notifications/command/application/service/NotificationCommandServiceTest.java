package com.deveagles.be15_deveagles_be.features.notifications.command.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.deveagles.be15_deveagles_be.features.notifications.command.application.dto.CreateNotificationRequest;
import com.deveagles.be15_deveagles_be.features.notifications.command.domain.aggregate.Notification;
import com.deveagles.be15_deveagles_be.features.notifications.command.domain.aggregate.NotificationType;
import com.deveagles.be15_deveagles_be.features.notifications.command.domain.repository.NotificationRepository;
import com.deveagles.be15_deveagles_be.features.notifications.query.application.dto.NotificationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class) // Mockito 기능을 JUnit 5와 통합
class NotificationCommandServiceTest {

  @InjectMocks // 테스트 대상 클래스에 @Mock으로 생성된 객체를 자동으로 주입
  private NotificationCommandService notificationCommandService;

  @Mock // 가짜(Mock) 객체 생성
  private NotificationRepository notificationRepository;

  @Test
  @DisplayName("알림 생성 성공 테스트")
  void create_notification_success() {
    // given (테스트 준비)
    final Long shopId = 1L;
    final String title = "테스트 제목";
    final String content = "테스트 내용입니다.";
    final NotificationType type = NotificationType.RESERVATION;

    CreateNotificationRequest request = new CreateNotificationRequest(shopId, type, title, content);

    // DB에 저장될 가상의 Notification 객체 생성
    Notification savedNotification =
        Notification.builder().shopId(shopId).title(title).content(content).type(type).build();

    // Mockito를 사용하여 notificationRepository.save()가 호출될 때,
    // 위에서 만든 savedNotification 객체를 반환하도록 설정
    given(notificationRepository.save(any(Notification.class))).willReturn(savedNotification);

    // when (테스트할 메서드 실행)
    NotificationResponse response = notificationCommandService.create(request);

    // then (결과 검증)

    // 1. 반환된 DTO가 null이 아닌지 확인
    assertThat(response).isNotNull();

    // 2. 반환된 DTO의 내용이 요청한 내용과 일치하는지 확인
    assertThat(response.getTitle()).isEqualTo(title);
    assertThat(response.getContent()).isEqualTo(content);
    assertThat(response.getType()).isEqualTo(type);

    // 3. repository의 save 메서드가 정확히 1번 호출되었는지 확인
    verify(notificationRepository).save(any(Notification.class));

    // 4. (심화) 실제로 repository에 전달된 Notification 객체의 값을 검증
    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationRepository).save(captor.capture());
    Notification capturedNotification = captor.getValue();

    assertThat(capturedNotification.getShopId()).isEqualTo(shopId);
    assertThat(capturedNotification.getTitle()).isEqualTo(title);
  }
}
