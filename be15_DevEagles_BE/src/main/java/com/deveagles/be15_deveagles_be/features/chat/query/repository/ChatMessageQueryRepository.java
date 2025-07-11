package com.deveagles.be15_deveagles_be.features.chat.query.repository;

import com.deveagles.be15_deveagles_be.features.chat.command.domain.aggregate.ChatMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageQueryRepository extends MongoRepository<ChatMessage, String> {
  Optional<ChatMessage> findTopByChatroomIdOrderByCreatedAtDesc(String chatroomId);

  List<ChatMessage> findByChatroomIdOrderByCreatedAtAsc(String roomId);

  ChatMessage findFirstByChatroomIdOrderByCreatedAtDesc(String chatroomId);
}
