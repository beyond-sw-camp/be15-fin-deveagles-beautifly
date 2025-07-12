package com.deveagles.be15_deveagles_be.features.chat.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.chat.command.domain.aggregate.ChatMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
  List<ChatMessage> findByChatroomIdOrderByCreatedAtAsc(String chatroomId);

  Optional<ChatMessage> findTopByChatroomIdOrderByCreatedAtDesc(String chatroomId);
}
