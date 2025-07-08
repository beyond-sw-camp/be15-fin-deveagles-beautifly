package com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request;

import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Customer;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageKind;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageSendingType;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageTemplate;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageType;
import java.time.LocalDateTime;
import java.util.List;

public record SmsRequest(
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
    Long workflowId) {
  public static SmsRequest ofForAutoSend(MessageTemplate template, Customer customer) {
    return new SmsRequest(
        List.of(customer.getId()),
        template.getTemplateContent(),
        MessageType.SMS, // 🔥 무조건 SMS로 고정
        MessageSendingType.AUTOMATIC,
        null, // scheduledAt: 즉시 발송
        template.getTemplateId(),
        false, // hasLink: 기본값
        customer.getCustomerGradeId(),
        null, // tagId
        MessageKind.announcement, // 🔥 알림 메시지로 고정
        null, // couponId
        null // workflowId
        );
  }
}
