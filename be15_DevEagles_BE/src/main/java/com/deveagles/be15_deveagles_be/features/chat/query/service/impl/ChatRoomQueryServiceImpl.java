package com.deveagles.be15_deveagles_be.features.chat.query.service.impl;

import com.deveagles.be15_deveagles_be.features.chat.command.domain.aggregate.ChatMessage;
import com.deveagles.be15_deveagles_be.features.chat.command.domain.aggregate.ChatRoom;
import com.deveagles.be15_deveagles_be.features.chat.command.domain.repository.ChatMessageRepository;
import com.deveagles.be15_deveagles_be.features.chat.query.dto.response.ChatRoomSummaryResponse;
import com.deveagles.be15_deveagles_be.features.chat.query.repository.ChatRoomQueryRepository;
import com.deveagles.be15_deveagles_be.features.chat.query.service.ChatRoomQueryService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomQueryServiceImpl implements ChatRoomQueryService {

  private final ChatRoomQueryRepository chatRoomQueryRepository;
  private final ChatMessageRepository chatMessageRepository;

  @Override
  public List<ChatRoomSummaryResponse> getMyChatRooms(Long userId, boolean isStaff) {
    List<ChatRoom> rooms =
        isStaff
            ? chatRoomQueryRepository.findByAssignedStaffIdAndIsAiActiveFalse(userId)
            : chatRoomQueryRepository.findByParticipantId(userId);

    return rooms.stream().map(room -> toSummaryResponse(room, isStaff)).toList();
  }

  private ChatRoomSummaryResponse toSummaryResponse(ChatRoom room, boolean isStaff) {
    Optional<ChatMessage> latestMessageOpt =
        chatMessageRepository.findTopByChatroomIdOrderByCreatedAtDesc(room.getId());

    LocalDateTime lastMessageAt = latestMessageOpt.map(ChatMessage::getCreatedAt).orElse(null);

    if (isStaff) {
      ChatRoom.Participant p = room.getParticipant();
      return ChatRoomSummaryResponse.builder()
          .roomId(room.getId())
          .customerName(p.getName())
          .customerShopName(p.getShopName())
          .lastMessageAt(lastMessageAt)
          .build();
    } else {
      return ChatRoomSummaryResponse.builder()
          .roomId(room.getId())
          .lastMessageAt(lastMessageAt)
          .build();
    }
  }
}
