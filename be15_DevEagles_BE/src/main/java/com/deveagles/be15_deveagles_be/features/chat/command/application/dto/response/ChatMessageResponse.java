package com.deveagles.be15_deveagles_be.features.chat.command.application.dto.response;

import com.deveagles.be15_deveagles_be.features.chat.command.domain.aggregate.ChatMessage;
import java.time.LocalDateTime;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageResponse {
  private String messageId;
  private String chatroomId;
  private Long senderId;
  private String senderName;
  private String content;
  private boolean isCustomer;
  private LocalDateTime createdAt;

  public static ChatMessageResponse from(ChatMessage msg) {
    return ChatMessageResponse.builder()
        .messageId(msg.getId())
        .chatroomId(msg.getChatroomId())
        .senderId(msg.getSender().getId())
        .senderName(msg.getSender().getName())
        .content(msg.getContent())
        .isCustomer(msg.isCustomer())
        .createdAt(msg.getCreatedAt())
        .build();
  }
}
