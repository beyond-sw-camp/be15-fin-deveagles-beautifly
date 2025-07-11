package com.deveagles.be15_deveagles_be.features.chat.query.repository;

import com.deveagles.be15_deveagles_be.features.chat.command.domain.aggregate.ChatRoom;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ChatRoomQueryRepository extends MongoRepository<ChatRoom, String> {
  List<ChatRoom> findByAssignedStaffIdAndIsAiActiveFalse(Long assignedStaffId);

  @Query("{'participant.id': ?0}")
  List<ChatRoom> findByParticipantId(Long participantId);
}
