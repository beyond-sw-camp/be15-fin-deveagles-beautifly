package com.deveagles.be15_deveagles_be.features.messages.command.application.service.impl;

import static com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageDeliveryStatus.PENDING;
import static com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageDeliveryStatus.SENT;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.customers.query.service.CustomerQueryService;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.SmsSendUnit;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.SmsRequest;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.response.MessageSendResult;
import com.deveagles.be15_deveagles_be.features.messages.command.application.service.MessageCommandService;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageSendingType;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageSettings;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.Sms;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.repository.MessageSettingRepository;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.repository.SmsRepository;
import com.deveagles.be15_deveagles_be.features.messages.command.infrastructure.CoolSmsClient;
import com.deveagles.be15_deveagles_be.features.shops.command.application.service.ShopCommandService;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageCommandServiceImpl implements MessageCommandService {
  private final ShopCommandService shopCommandService;
  private final CustomerQueryService customerQueryService;
  private final MessageSettingRepository messageSettingRepository;
  private final CoolSmsClient coolSmsClient;
  private final SmsRepository smsRepository;

  @Override
  @Transactional
  public List<MessageSendResult> sendSms(SmsRequest smsRequest) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime scheduledAt = resolveScheduledAt(smsRequest, now);

    // 1. 유효성 검증
    shopCommandService.validateShopExists(smsRequest.shopId());

    // 2. 고객 번호 조회
    List<Long> customerIds = smsRequest.customerIds();
    List<String> phoneNumbers = customerQueryService.getCustomerPhoneNumbers(customerIds);
    // 3. 메시지 설정 조회
    MessageSettings settings =
        messageSettingRepository
            .findByShopId(smsRequest.shopId())
            .orElseThrow(() -> new BusinessException(ErrorCode.MESSAGE_SETTINGS_NOT_FOUND));
    String senderNumber = settings.getSenderNumber();

    // 4. 예약 여부 확인
    boolean isReservation = smsRequest.messageSendingType() == MessageSendingType.RESERVATION;

    // 5. 즉시 전송 처리
    List<Sms> smsList =
        IntStream.range(0, customerIds.size())
            .mapToObj(
                i ->
                    Sms.builder()
                        .shopId(smsRequest.shopId())
                        .customerId(customerIds.get(i))
                        .messageContent(smsRequest.messageContent())
                        .messageKind(smsRequest.messageKind())
                        .messageType(smsRequest.messageType())
                        .messageSendingType(smsRequest.messageSendingType())
                        .messageDeliveryStatus(isReservation ? PENDING : SENT)
                        .scheduledAt(scheduledAt)
                        .templateId(smsRequest.templateId())
                        .sentAt(now)
                        .build())
            .toList();

    List<Sms> saved = smsRepository.saveAll(smsList);

    // 6. 즉시 발송이면 이벤트 발행
    if (!isReservation) {
      List<SmsSendUnit> units =
          IntStream.range(0, saved.size())
              .mapToObj(i -> new SmsSendUnit(saved.get(i).getMessageId(), phoneNumbers.get(i)))
              .toList();

      List<MessageSendResult> results =
          coolSmsClient.sendMany(senderNumber, smsRequest.messageContent(), units);

      List<Long> failedIds =
          results.stream().filter(r -> !r.success()).map(MessageSendResult::messageId).toList();

      if (!failedIds.isEmpty()) {
        markSmsAsFailed(failedIds);
      }

      return results;
    }

    // 7. (지금은 실행되지 않음)
    return List.of(); // 예약 발송 처리 안 하므로 빈 응답
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void markSmsAsFailed(Collection<Long> smsIds) {
    List<Sms> failedMessages = smsRepository.findAllById(smsIds);
    failedMessages.forEach(Sms::markAsFailed);
    smsRepository.saveAll(failedMessages);
  }

  private LocalDateTime resolveScheduledAt(SmsRequest request, LocalDateTime now) {
    if (request.messageSendingType() == MessageSendingType.RESERVATION
        && request.scheduledAt() == null) {
      throw new BusinessException(ErrorCode.SCHEDULE_TIME_REQUIRED_FOR_RESERVATION);
    }

    if (request.messageSendingType() == MessageSendingType.IMMEDIATE
        && request.scheduledAt() != null) {
      throw new BusinessException(ErrorCode.SCHEDULE_TIME_NOT_ALLOWED_FOR_IMMEDIATE);
    }

    return request.messageSendingType() == MessageSendingType.IMMEDIATE
        ? now
        : request.scheduledAt();
  }
}
