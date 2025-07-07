package com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request;

import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageKind;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageSendingType;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageType;
import java.time.LocalDateTime;
import java.util.List;

public record SmsRequest(
    Long shopId,
    List<Long> customerIds,
    String messageContent,
    MessageType messageType,
    MessageSendingType messageSendingType,
    LocalDateTime scheduledAt,
    Long templateId,
    Boolean hasLink,
    Long customerGradeId,
    Long tagId,
    MessageKind messageKind,
    Long couponId,
    Long workflowId) {}
