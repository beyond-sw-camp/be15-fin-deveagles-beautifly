package com.deveagles.be15_deveagles_be.features.chat.command.domain.aggregate;

import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessage {

  @Id private String id;

  private String chatroomId;

  private Sender sender;

  private String content;

  private boolean isCustomer;

  private LocalDateTime createdAt;

  private LocalDateTime deletedAt;

  @Getter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @AllArgsConstructor
  @Builder
  public static class Sender {
    private Long id; // 고객 ID는 Long, 상담사/AI는 null
    private String name;
  }
}
