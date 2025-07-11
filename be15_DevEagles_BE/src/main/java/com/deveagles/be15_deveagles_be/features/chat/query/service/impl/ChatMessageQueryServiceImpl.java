package com.deveagles.be15_deveagles_be.features.chat.query.service.impl;

import com.deveagles.be15_deveagles_be.features.chat.command.application.dto.response.ChatMessageResponse;
import com.deveagles.be15_deveagles_be.features.chat.command.domain.aggregate.ChatMessage;
import com.deveagles.be15_deveagles_be.features.chat.query.repository.ChatMessageQueryRepository;
import com.deveagles.be15_deveagles_be.features.chat.query.service.ChatMessageQueryService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageQueryServiceImpl implements ChatMessageQueryService {

  private final ChatMessageQueryRepository chatMessageQueryRepository;

  public ChatMessageQueryServiceImpl(ChatMessageQueryRepository chatMessageQueryRepository) {
    this.chatMessageQueryRepository = chatMessageQueryRepository;
  }

  @Override
  public List<ChatMessageResponse> getMessagesByRoomId(String roomId) {
    List<ChatMessage> messages =
        chatMessageQueryRepository.findByChatroomIdOrderByCreatedAtAsc(roomId);

    return messages.stream().map(ChatMessageResponse::from).toList();
  }
}
