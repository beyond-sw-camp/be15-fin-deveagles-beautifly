package com.deveagles.be15_deveagles_be.features.chat.command.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRequest {
  private String roomId; // 채팅방 ID (Mongo _id)
  private String senderName; // 보낸 사람 이름
  private String content; // 메시지 내용
  private boolean isCustomer; // 고객 여부 (true: 고객, false: 상담사)
}
