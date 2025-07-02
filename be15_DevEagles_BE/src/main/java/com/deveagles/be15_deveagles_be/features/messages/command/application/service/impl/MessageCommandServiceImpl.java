package com.deveagles.be15_deveagles_be.features.messages.command.application.service.impl;

import static com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageDeliveryStatus.PENDING;
import static com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageDeliveryStatus.SENT;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.customers.query.service.CustomerQueryService;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.SmsRequest;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.SmsSendEvent;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.response.SmsResponse;
import com.deveagles.be15_deveagles_be.features.messages.command.application.service.MessageCommandService;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageSendingType;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageSettings;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.Sms;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.repository.MessageSettingRepository;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.repository.SmsRepository;
import com.deveagles.be15_deveagles_be.features.shops.command.application.service.ShopCommandService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MessageCommandServiceImpl implements MessageCommandService {
  private final ShopCommandService shopCommandService;
  private final CustomerQueryService customerQueryService;
  private final MessageSettingRepository messageSettingRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final SmsRepository smsRepository;

  @Override
  @Transactional
  public SmsResponse sendSms(SmsRequest smsRequest) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime scheduledAt = resolveScheduledAt(smsRequest, now);

    // 1. 유효성 검증
    shopCommandService.validateShopExists(smsRequest.shopId());
    // 2. 고객 번호 조회
    String phoneNumber = customerQueryService.getCustomerPhoneNumber(smsRequest.customerId());
    // 3. 메시지 설정 조회
    MessageSettings settings =
        messageSettingRepository
            .findByShopId(smsRequest.shopId())
            .orElseThrow(() -> new BusinessException(ErrorCode.MESSAGE_SETTINGS_NOT_FOUND));
    String senderNumber = settings.getSenderNumber();

    // 4. 예약 여부 확인
    boolean isReservation = smsRequest.messageSendingType() == MessageSendingType.RESERVATION;

    // 5. 즉시 전송 처리
    Sms sms =
        Sms.builder()
            .shopId(smsRequest.shopId())
            .customerId(smsRequest.customerId())
            .messageContent(smsRequest.messageContent())
            .messageKind(smsRequest.messageKind())
            .messageType(smsRequest.messageType())
            .messageSendingType(smsRequest.messageSendingType())
            .messageDeliveryStatus(isReservation ? PENDING : SENT)
            .scheduledAt(scheduledAt)
            .templateId(smsRequest.templateId())
            .sentAt(now)
            .build();

    Sms saved = smsRepository.save(sms);

    if (!isReservation) {
      eventPublisher.publishEvent(
          new SmsSendEvent(
              saved.getMessageId(), senderNumber, phoneNumber, smsRequest.messageContent()));
    }

    return SmsResponse.from(saved);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void markSmsAsFailed(Long smsId) {
    smsRepository
        .findById(smsId)
        .ifPresent(
            sms -> {
              sms.markAsFailed();
              smsRepository.save(sms);
            });
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
