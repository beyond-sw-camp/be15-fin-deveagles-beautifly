package com.deveagles.be15_deveagles_be.features.messages.command.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.customers.query.service.CustomerQueryService;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.SmsRequest;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.UpdateReservationRequest;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.response.MessageSendResult;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.*;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.repository.MessageSettingRepository;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.repository.SmsRepository;
import com.deveagles.be15_deveagles_be.features.messages.command.infrastructure.CoolSmsClient;
import com.deveagles.be15_deveagles_be.features.shops.command.application.service.ShopCommandService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@DisplayName("메시지 커맨드 서비스 테스트")
@ExtendWith(MockitoExtension.class)
class MessageCommandServiceImplTest {

  @InjectMocks private MessageCommandServiceImpl messageCommandService;

  @Mock private CustomerQueryService customerQueryService;
  @Mock private MessageSettingRepository messageSettingRepository;
  @Mock private CoolSmsClient coolSmsClient;
  @Mock private SmsRepository smsRepository;
  @Mock private ShopCommandService shopCommandService;

  private final Long shopId = 1L;
  private final Long customerId = 10L;
  private final String content = "테스트 메시지입니다";

  @Test
  void sendSms_IMMEDIATE_성공() {
    // given
    SmsRequest request =
        new SmsRequest(
            shopId,
            List.of(customerId),
            content,
            MessageType.SMS,
            MessageSendingType.IMMEDIATE,
            null,
            1L,
            true,
            null,
            null,
            MessageKind.announcement,
            null,
            null);

    MessageSettings settings =
        MessageSettings.builder().shopId(shopId).senderNumber("01012345678").build();

    List<String> phoneNumbers = List.of("01099998888");

    given(customerQueryService.getCustomerPhoneNumbers(any())).willReturn(phoneNumbers);
    given(messageSettingRepository.findByShopId(shopId)).willReturn(Optional.of(settings));
    given(smsRepository.saveAll(any())).willAnswer(invocation -> invocation.getArgument(0));
    given(coolSmsClient.sendMany(any(), any(), any()))
        .willReturn(List.of(new MessageSendResult(true, "성공", 1L)));

    // when
    List<MessageSendResult> result = messageCommandService.sendSms(shopId, request);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).success()).isTrue();
    assertThat(result.get(0).resultMessage()).isEqualTo("성공");
    verify(shopCommandService, times(2)).validateShopExists(shopId);
  }

  @Test
  void sendSms_RESERVATION_성공() {
    // given
    LocalDateTime future = LocalDateTime.now().plusMinutes(10);
    SmsRequest request =
        new SmsRequest(
            shopId,
            List.of(customerId),
            content,
            MessageType.SMS,
            MessageSendingType.RESERVATION,
            future,
            1L,
            false,
            null,
            null,
            MessageKind.announcement,
            null,
            null);

    MessageSettings settings =
        MessageSettings.builder().shopId(shopId).senderNumber("01012345678").build();

    given(customerQueryService.getCustomerPhoneNumbers(any())).willReturn(List.of("01012345678"));
    given(messageSettingRepository.findByShopId(shopId)).willReturn(Optional.of(settings));
    given(smsRepository.saveAll(any())).willAnswer(invocation -> invocation.getArgument(0));

    // when
    List<MessageSendResult> result = messageCommandService.sendSms(shopId, request);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).resultMessage()).isEqualTo("예약 등록 완료");
    verify(shopCommandService, times(2)).validateShopExists(shopId);
  }

  @Test
  void updateReservationMessage_실패_과거시간() {
    // given
    Long messageId = 100L;
    LocalDateTime oldTime = LocalDateTime.now().minusMinutes(5);

    Sms sms =
        Sms.builder()
            .messageId(messageId)
            .shopId(shopId)
            .scheduledAt(LocalDateTime.now().plusMinutes(10))
            .build();

    given(smsRepository.findById(messageId)).willReturn(Optional.of(sms));

    UpdateReservationRequest req =
        new UpdateReservationRequest("수정됨", MessageKind.advertising, customerId, oldTime);

    // then
    assertThatThrownBy(() -> messageCommandService.updateReservationMessage(req, shopId, messageId))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.INVALID_SCHEDULED_TIME.getMessage());

    verify(shopCommandService).validateShopExists(shopId);
  }

  @Test
  void cancelScheduledMessage_성공() {
    // given
    Long messageId = 200L;
    Sms sms =
        Sms.builder()
            .messageId(messageId)
            .shopId(shopId)
            .scheduledAt(LocalDateTime.now().plusMinutes(20))
            .messageDeliveryStatus(MessageDeliveryStatus.PENDING)
            .messageSendingType(MessageSendingType.RESERVATION)
            .build();

    given(smsRepository.findById(messageId)).willReturn(Optional.of(sms));

    // when
    messageCommandService.cancelScheduledMessage(messageId, shopId);

    // then
    assertThat(sms.getMessageDeliveryStatus()).isEqualTo(MessageDeliveryStatus.CANCELLED);
    verify(shopCommandService).validateShopExists(shopId);
  }

  @Test
  void markSmsAsFailed_성공() {
    // given
    Sms sms =
        Sms.builder().messageId(1L).messageDeliveryStatus(MessageDeliveryStatus.PENDING).build();

    given(smsRepository.findAllById(any())).willReturn(List.of(sms));

    // when
    messageCommandService.markSmsAsFailed(List.of(1L));

    // then
    assertThat(sms.getMessageDeliveryStatus()).isEqualTo(MessageDeliveryStatus.FAIL);
  }

  @Test
  void markSmsAsSent_성공() {
    // given
    Sms sms =
        Sms.builder().messageId(1L).messageDeliveryStatus(MessageDeliveryStatus.PENDING).build();

    given(smsRepository.findAllById(any())).willReturn(List.of(sms));

    // when
    messageCommandService.markSmsAsSent(List.of(1L));

    // then
    assertThat(sms.getMessageDeliveryStatus()).isEqualTo(MessageDeliveryStatus.SENT);
  }
}
