package com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request;

import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageKind;
import java.time.LocalDateTime;

public record UpdateReservationRequest(
    String messageContent, MessageKind messageKind, Long customerId, LocalDateTime scheduledAt) {}
