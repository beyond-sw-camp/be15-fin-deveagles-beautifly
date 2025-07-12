package com.deveagles.be15_deveagles_be.features.chat.command.application.service;

import com.deveagles.be15_deveagles_be.features.chat.command.application.dto.response.ChatRoomCreateResult;

public interface ChatRoomService {
  ChatRoomCreateResult createChatRoom(Long shopId, Long staffId, String staffName);

  void sendGreeting(String roomId);

  void switchToStaff(String roomId);
}
