package com.deveagles.be15_deveagles_be.features.messages.command.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.deveagles.be15_deveagles_be.features.customers.query.service.CustomerQueryService;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.SmsRequest;
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

@ExtendWith(MockitoExtension.class)
@DisplayName("메시지 커맨드 서비스 테스트")
class MessageCommandServiceImplTest {

  @Mock private ShopCommandService shopCommandService;
  @Mock private CustomerQueryService customerQueryService;
  @Mock private MessageSettingRepository messageSettingRepository;
  @Mock private CoolSmsClient coolSmsClient;
  @Mock private SmsRepository smsRepository;

  @InjectMocks private MessageCommandServiceImpl messageCommandService;

  @Test
  @DisplayName("즉시 메시지 전송 성공")
  void sendSms_ImmediateSuccess() {
    // given
    SmsRequest request =
        new SmsRequest(
            1L,
            List.of(101L, 102L),
            "공지입니다.",
            MessageType.SMS,
            MessageSendingType.IMMEDIATE,
            null,
            null,
            false,
            null,
            null,
            MessageKind.announcement);

    given(customerQueryService.getCustomerPhoneNumbers(request.customerIds()))
        .willReturn(List.of("01012345678", "01023456789"));

    MessageSettings settings =
        MessageSettings.builder()
            .shopId(1L)
            .senderNumber("029302930")
            .point(100L)
            .canAlimtalk(false)
            .build();

    given(messageSettingRepository.findByShopId(1L)).willReturn(Optional.of(settings));

    List<Sms> smsList =
        request.customerIds().stream()
            .map(
                cid ->
                    Sms.builder()
                        .messageId(cid + 1000)
                        .shopId(1L)
                        .customerId(cid)
                        .messageContent(request.messageContent())
                        .messageKind(request.messageKind())
                        .messageType(request.messageType())
                        .messageSendingType(request.messageSendingType())
                        .scheduledAt(LocalDateTime.now())
                        .sentAt(LocalDateTime.now())
                        .messageDeliveryStatus(MessageDeliveryStatus.SENT)
                        .build())
            .toList();

    given(smsRepository.saveAll(any())).willReturn(smsList);

    List<MessageSendResult> fakeResult =
        List.of(
            new MessageSendResult(true, "발송 성공", 1101L),
            new MessageSendResult(false, "발송 실패", 1102L));

    given(coolSmsClient.sendMany(any(), any(), any())).willReturn(fakeResult);

    // when
    List<MessageSendResult> result = messageCommandService.sendSms(request);

    // then
    assertThat(result).hasSize(2);
    assertThat(result.get(0).success()).isTrue();
    assertThat(result.get(1).success()).isFalse();

    then(smsRepository).should(times(2)).saveAll(any());
    then(coolSmsClient).should().sendMany(any(), any(), any());
  }

  @Test
  @DisplayName("메시지 전송 실패 시 실패 처리 확인")
  void sendSms_FailureHandledCorrectly() {
    // given
    SmsRequest request =
        new SmsRequest(
            1L,
            List.of(201L),
            "이벤트 안내",
            MessageType.SMS,
            MessageSendingType.IMMEDIATE,
            null,
            null,
            false,
            null,
            null,
            MessageKind.advertising);

    given(customerQueryService.getCustomerPhoneNumbers(request.customerIds()))
        .willReturn(List.of("01012345678"));

    MessageSettings settings =
        MessageSettings.builder()
            .shopId(1L)
            .senderNumber("0505050505")
            .point(200L)
            .canAlimtalk(true)
            .build();

    given(messageSettingRepository.findByShopId(1L)).willReturn(Optional.of(settings));

    Sms saved =
        Sms.builder()
            .messageId(3001L)
            .shopId(1L)
            .customerId(201L)
            .messageContent("이벤트 안내")
            .messageKind(MessageKind.advertising)
            .messageType(MessageType.SMS)
            .messageSendingType(MessageSendingType.IMMEDIATE)
            .messageDeliveryStatus(MessageDeliveryStatus.SENT)
            .scheduledAt(LocalDateTime.now())
            .sentAt(LocalDateTime.now())
            .build();

    given(smsRepository.saveAll(any())).willReturn(List.of(saved));

    given(coolSmsClient.sendMany(any(), any(), any()))
        .willReturn(List.of(new MessageSendResult(false, "발송 실패", 3001L)));

    // when
    List<MessageSendResult> result = messageCommandService.sendSms(request);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).success()).isFalse();
    then(smsRepository).should(times(2)).saveAll(any());
    then(coolSmsClient).should().sendMany(any(), any(), any());
  }
}
