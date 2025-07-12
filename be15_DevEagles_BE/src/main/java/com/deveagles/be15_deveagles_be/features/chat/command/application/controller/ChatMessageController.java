package com.deveagles.be15_deveagles_be.features.chat.command.application.controller;

import com.deveagles.be15_deveagles_be.features.chat.command.application.dto.request.ChatMessageRequest;
import com.deveagles.be15_deveagles_be.features.chat.command.application.service.ChatService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping
public class ChatMessageController {

  private final ChatService chatService;

  // ChatController
  @MessageMapping("/chat/send")
  public void sendMessage(@Valid @Payload ChatMessageRequest request, Principal principal) {
    Long userId = Long.parseLong(principal.getName());
    chatService.saveMessage(request, userId); // 내부에서 convertAndSend 함
  }
}
