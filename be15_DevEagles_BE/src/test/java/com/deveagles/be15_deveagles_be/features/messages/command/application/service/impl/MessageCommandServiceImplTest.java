package com.deveagles.be15_deveagles_be.features.messages.command.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.features.customers.query.service.CustomerQueryService;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.SmsRequest;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.UpdateReservationRequest;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.response.MessageSendResult;
import com.deveagles.be15_deveagles_be.features.messages.command.application.service.MessageVariableProcessor;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.*;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.repository.MessageSettingRepository;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.repository.SmsRepository;
import com.deveagles.be15_deveagles_be.features.messages.command.infrastructure.CoolSmsClient;
import com.deveagles.be15_deveagles_be.features.shops.command.application.service.ShopCommandService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageCommandServiceImplTest {

  @InjectMocks private MessageCommandServiceImpl messageCommandService;

  @Mock private ShopCommandService shopCommandService;
  @Mock private CustomerQueryService customerQueryService;
  @Mock private MessageSettingRepository messageSettingRepository;
  @Mock private CoolSmsClient coolSmsClient;
  @Mock private SmsRepository smsRepository;
  @Mock private MessageVariableProcessor messageVariableProcessor;

  @Test
  @DisplayName("즉시 발송 성공")
  void sendSms_immediate_success() {
    // given
    Long shopId = 1L;
    List<Long> customerIds = List.of(1L, 2L);
    List<String> phoneNumbers = List.of("01011112222", "01033334444");

    SmsRequest request =
        new SmsRequest(
            customerIds,
            "안녕하세요 #{고객명}",
            MessageType.SMS,
            MessageSendingType.IMMEDIATE,
            null,
            10L,
            true,
            11L,
            12L,
            MessageKind.announcement,
            13L,
            14L);

    MessageSettings settings =
        MessageSettings.builder()
            .shopId(shopId)
            .senderNumber("07000000000")
            .point(1000L)
            .canAlimtalk(true)
            .build();

    // validateShopExists()는 void → stub 없이 호출만 되게 둠 (when 제거)
    when(customerQueryService.getCustomerPhoneNumbers(anyList())).thenReturn(phoneNumbers);
    when(messageSettingRepository.findByShopId(shopId)).thenReturn(Optional.of(settings));
    when(messageVariableProcessor.buildPayload(anyLong(), eq(shopId)))
        .thenReturn(Map.of("고객명", "홍길동"));
    when(messageVariableProcessor.resolveVariables(anyString(), anyMap())).thenReturn("안녕하세요 홍길동");

    List<Sms> saved =
        List.of(
            Sms.builder().messageId(1L).messageContent("안녕하세요 홍길동").build(),
            Sms.builder().messageId(2L).messageContent("안녕하세요 홍길동").build());
    when(smsRepository.saveAll(anyList())).thenReturn(saved);

    List<MessageSendResult> sendResults =
        List.of(new MessageSendResult(true, "성공", 1L), new MessageSendResult(true, "성공", 2L));
    when(coolSmsClient.sendMany(any(), any(), anyList())).thenReturn(sendResults);

    // when
    List<MessageSendResult> result = messageCommandService.sendSms(shopId, request);

    // then
    assertThat(result).hasSize(2);
    assertThat(result).allMatch(MessageSendResult::success);
    verify(coolSmsClient, times(1)).sendMany(any(), any(), anyList());
    verify(shopCommandService).validateShopExists(shopId); // 호출 여부만 검증
  }

  @Test
  @DisplayName("예약 발송 성공")
  void sendSms_reservation_success() {
    // given
    Long shopId = 1L;
    LocalDateTime scheduledAt = LocalDateTime.now().plusMinutes(10);
    SmsRequest request =
        new SmsRequest(
            List.of(100L),
            "예약 발송 #{고객명}",
            MessageType.SMS,
            MessageSendingType.RESERVATION,
            scheduledAt,
            null,
            false,
            null,
            null,
            MessageKind.announcement,
            null,
            null);

    when(customerQueryService.getCustomerPhoneNumbers(any())).thenReturn(List.of("01022223333"));
    when(messageSettingRepository.findByShopId(shopId))
        .thenReturn(
            Optional.of(
                MessageSettings.builder()
                    .shopId(shopId)
                    .senderNumber("07012345678")
                    .canAlimtalk(true)
                    .point(100L)
                    .build()));
    when(messageVariableProcessor.buildPayload(anyLong(), eq(shopId)))
        .thenReturn(Map.of("고객명", "신사임당"));
    when(messageVariableProcessor.resolveVariables(anyString(), anyMap())).thenReturn("예약 발송 신사임당");

    List<Sms> saved = List.of(Sms.builder().messageId(3L).messageContent("예약 발송 신사임당").build());
    when(smsRepository.saveAll(anyList())).thenReturn(saved);

    // when
    List<MessageSendResult> result = messageCommandService.sendSms(shopId, request);

    // then
    assertThat(result).hasSize(1);
    assertThat(result.get(0).success()).isTrue();
    assertThat(result.get(0).resultMessage()).isEqualTo("예약 등록 완료");
    verifyNoInteractions(coolSmsClient);
  }

  @Test
  @DisplayName("메시지 설정 없음 - 예외 발생")
  void sendSms_fail_no_message_settings() {
    // given
    Long shopId = 1L;
    SmsRequest request =
        new SmsRequest(
            List.of(1L),
            "내용",
            MessageType.SMS,
            MessageSendingType.IMMEDIATE,
            null,
            null,
            false,
            null,
            null,
            MessageKind.announcement,
            null,
            null);

    when(messageSettingRepository.findByShopId(shopId)).thenReturn(Optional.empty());

    // expect
    assertThatThrownBy(() -> messageCommandService.sendSms(shopId, request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("메시지 설정이 존재하지 않습니다");
  }

  @Test
  @DisplayName("예약 메시지 수정 성공")
  void updateReservationMessage_success() {
    // given
    Long shopId = 1L;
    Long messageId = 1L;
    LocalDateTime futureTime = LocalDateTime.now().plusHours(1);
    UpdateReservationRequest updateRequest =
        new UpdateReservationRequest("수정된 메시지", MessageKind.announcement, 123L, futureTime);

    Sms sms = Mockito.mock(Sms.class);

    // 👉 실제 메서드 내에서 호출되는 메서드만 stubbing
    when(sms.getShopId()).thenReturn(shopId);
    when(sms.getScheduledAt()).thenReturn(futureTime);
    when(smsRepository.findById(messageId)).thenReturn(Optional.of(sms));

    // when
    messageCommandService.updateReservationMessage(updateRequest, shopId, messageId);

    // then
    verify(sms).updateReservation("수정된 메시지", MessageKind.announcement, 123L, futureTime);
  }

  @Test
  @DisplayName("예약 메시지 취소 성공")
  void cancelScheduledMessage_success() {
    // given
    Long shopId = 1L;
    Long messageId = 1L;

    Sms sms = Mockito.mock(Sms.class);
    when(sms.getShopId()).thenReturn(shopId);
    when(sms.isReservable()).thenReturn(true);
    when(smsRepository.findById(messageId)).thenReturn(Optional.of(sms));

    // when
    messageCommandService.cancelScheduledMessage(messageId, shopId);

    // then
    verify(sms).cancel();
  }

  @Test
  @DisplayName("실패 메시지 상태 업데이트 성공")
  void markSmsAsFailed_success() {
    // given
    List<Long> failIds = List.of(1L, 2L);
    List<Sms> messages = List.of(Mockito.mock(Sms.class), Mockito.mock(Sms.class));
    when(smsRepository.findAllById(failIds)).thenReturn(messages);

    // when
    messageCommandService.markSmsAsFailed(failIds);

    // then
    messages.forEach(m -> verify(m).markAsFailed());
    verify(smsRepository).saveAll(messages);
  }

  @Test
  @DisplayName("성공 메시지 상태 업데이트 성공")
  void markSmsAsSent_success() {
    // given
    List<Long> successIds = List.of(3L, 4L);
    List<Sms> messages = List.of(Mockito.mock(Sms.class), Mockito.mock(Sms.class));
    when(smsRepository.findAllById(successIds)).thenReturn(messages);

    // when
    messageCommandService.markSmsAsSent(successIds);

    // then
    messages.forEach(m -> verify(m).markAsSent());
    verify(smsRepository).saveAll(messages);
  }
}
