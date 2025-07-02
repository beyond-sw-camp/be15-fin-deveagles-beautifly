package com.deveagles.be15_deveagles_be.features.messages.command.application.service;

import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.SmsGroupSendEvent;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.SmsSendUnit;
import com.deveagles.be15_deveagles_be.features.messages.command.infrastructure.CoolSmsClient;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class SmsSendEventListener {

  private final CoolSmsClient coolSmsClient;
  private final MessageCommandService messageCommandService;

  @Async
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(SmsGroupSendEvent event) {
    List<String> failedNumbers =
        coolSmsClient.sendMany(event.sender(), event.content(), event.units());

    Set<Long> failedMessageIds =
        event.units().stream()
            .filter(unit -> failedNumbers.contains(unit.phoneNumber()))
            .map(SmsSendUnit::messageId)
            .collect(Collectors.toSet());

    messageCommandService.markSmsAsFailed(failedMessageIds);
  }
}
