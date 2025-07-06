package com.deveagles.be15_deveagles_be.features.messages.query.dto.response;

import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.Sms;
import java.time.LocalDateTime;

public record SmsDetailResponse(
    Long messageId,
    String content,
    String status,
    LocalDateTime createdAt,
    LocalDateTime sentAt,
    LocalDateTime scheduledAt,
    String messageType,
    String messageSendingType,
    String messageKind,
    boolean hasLink,
    Long customerId,
    Long templateId,
    Long customerGradeId,
    Long tagId) {
  public static SmsDetailResponse from(Sms sms) {
    return new SmsDetailResponse(
        sms.getMessageId(),
        sms.getMessageContent(),
        sms.getMessageDeliveryStatus().name(),
        sms.getCreatedAt(),
        sms.getSentAt(),
        sms.getScheduledAt(),
        sms.getMessageType().name(),
        sms.getMessageSendingType().name(),
        sms.getMessageKind().name(),
        sms.getHasLink(),
        sms.getCustomerId(),
        sms.getTemplateId(),
        sms.getCustomerGradeId(),
        sms.getTagId());
  }
}
