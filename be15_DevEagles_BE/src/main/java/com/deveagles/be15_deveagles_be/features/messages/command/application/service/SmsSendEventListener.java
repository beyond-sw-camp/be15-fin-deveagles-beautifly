package com.deveagles.be15_deveagles_be.features.messages.command.application.service;

/*
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
*/
