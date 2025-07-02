package com.deveagles.be15_deveagles_be.features.messages.command.infrastructure;

import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.SmsSendUnit;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.response.MessageSendResult;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.FailedMessage;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.response.MultipleDetailMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CoolSmsClient {

  private DefaultMessageService messageService;

  @Value("${coolsms.apiKey}")
  private String apiKey;

  @Value("${coolsms.apiSecret}")
  private String apiSecret;

  @PostConstruct
  public void init() {
    this.messageService =
        NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
  }

  public List<MessageSendResult> sendMany(String sender, String content, List<SmsSendUnit> units) {
    List<Message> messages =
        units.stream()
            .map(
                unit -> {
                  Message message = new Message();
                  message.setFrom(sender);
                  message.setTo(unit.phoneNumber());
                  message.setText(content);
                  return message;
                })
            .toList();

    try {
      // 동기 호출
      MultipleDetailMessageSentResponse response = messageService.send(messages, false, true);

      // 실패한 번호 리스트 추출
      List<String> failedNumbers =
          response.getFailedMessageList().stream().map(FailedMessage::getTo).toList();

      // 각 유닛에 대해 결과 생성
      return units.stream()
          .map(
              unit -> {
                boolean isSuccess = !failedNumbers.contains(unit.phoneNumber());
                return new MessageSendResult(
                    isSuccess, isSuccess ? "발송 성공" : "발송 실패", unit.messageId());
              })
          .toList();

    } catch (Exception e) {
      log.error("CoolSMS sendMany 예외", e);
      String message = e.getMessage() != null ? e.getMessage() : "알 수 없는 예외 발생";

      // 예외 시 모든 메시지를 실패 처리
      return units.stream()
          .map(unit -> new MessageSendResult(false, "예외 발생: " + message, unit.messageId()))
          .toList();
    }
  }
}
