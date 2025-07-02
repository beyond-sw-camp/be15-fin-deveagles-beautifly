package com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request;

public record SmsSendEvent(
    Long smsId, String senderNumber, String receiverNumber, String content) {}
