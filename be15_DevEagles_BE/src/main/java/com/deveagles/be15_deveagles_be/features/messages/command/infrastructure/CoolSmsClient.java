package com.deveagles.be15_deveagles_be.features.messages.command.infrastructure;

import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.SmsSendUnit;
import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
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

  public List<String> sendMany(String sender, String content, List<SmsSendUnit> units) {
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
      MultipleDetailMessageSentResponse response = messageService.send(messages, false, true);

      if (!response.getFailedMessageList().isEmpty()) {
        return response.getFailedMessageList().stream()
            .map(FailedMessage::getTo)
            .collect(Collectors.toList());
      }

      return List.of(); // 전체 성공
    } catch (Exception e) {
      log.error("단체 메시지 전송 중 예외 발생", e);

      // 전체 실패한 것으로 간주
      return messages.stream().map(Message::getTo).collect(Collectors.toList());
    }
  }
}
