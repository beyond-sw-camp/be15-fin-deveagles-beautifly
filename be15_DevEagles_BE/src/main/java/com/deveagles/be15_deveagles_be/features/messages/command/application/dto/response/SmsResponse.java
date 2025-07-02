package com.deveagles.be15_deveagles_be.features.messages.command.application.dto.response;

import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageDeliveryStatus;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.Sms;
import java.time.LocalDateTime;

public record SmsResponse(Long messageId, MessageDeliveryStatus status, LocalDateTime scheduledAt) {
  public static SmsResponse from(Sms sms) {
    return new SmsResponse(
        sms.getMessageId(), sms.getMessageDeliveryStatus(), sms.getScheduledAt());
  }
}
