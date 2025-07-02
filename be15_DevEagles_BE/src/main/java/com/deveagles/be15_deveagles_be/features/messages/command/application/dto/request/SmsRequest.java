package com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request;

import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageKind;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageSendingType;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageType;
import java.time.LocalDateTime;

public record SmsRequest(
    Long shopId,
    Long customerId,
    String messageContent,
    MessageType messageType,
    MessageSendingType messageSendingType,
    LocalDateTime scheduledAt,
    Long templateId,
    Boolean hasLink,
    Long customerGradeId,
    Long tagId,
    MessageKind messageKind) {}
