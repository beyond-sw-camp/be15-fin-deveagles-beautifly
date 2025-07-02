package com.deveagles.be15_deveagles_be.features.messages.command.infrastructure;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
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

  public void sendSms(String from, String to, String text) {
    Message message = new Message();
    message.setFrom(from);
    message.setTo(to);
    message.setText(text);

    try {
      messageService.sendOne(new SingleMessageSendingRequest(message));

    } catch (Exception e) {
      throw new RuntimeException("문자 발송 실패", e);
    }
  }

  // 간단한 마스킹 함수
  private String mask(String key) {
    if (key.length() <= 4) return "****";
    return key.substring(0, 2) + "****" + key.substring(key.length() - 2);
  }
}
