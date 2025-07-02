package com.deveagles.be15_deveagles_be.features.messages.command.application.service;

import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.SmsSendEvent;
import com.deveagles.be15_deveagles_be.features.messages.command.infrastructure.CoolSmsClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsSendEventListener {
  private final CoolSmsClient coolSmsClient;
  private final MessageCommandService messageCommandService;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(SmsSendEvent event) {
    try {
      coolSmsClient.sendSms(event.senderNumber(), event.receiverNumber(), event.content());
    } catch (Exception e) {
      messageCommandService.markSmsAsFailed(event.smsId());
    }
  }
}
