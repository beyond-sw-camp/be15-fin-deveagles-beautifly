package com.deveagles.be15_deveagles_be.features.messages.command.application.dto.response;

public record MessageSendResult(boolean success, String resultMessage, Long messageId) {}
