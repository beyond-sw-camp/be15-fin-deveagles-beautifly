package com.deveagles.be15_deveagles_be.features.chat.query.service;

import com.deveagles.be15_deveagles_be.features.chat.command.application.dto.response.ChatMessageResponse;
import java.util.List;

public interface ChatMessageQueryService {
  List<ChatMessageResponse> getMessagesByRoomId(String roomId);
}
