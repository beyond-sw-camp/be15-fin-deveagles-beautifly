package com.deveagles.be15_deveagles_be.features.chat.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.chat.command.application.dto.response.ChatMessageResponse;
import com.deveagles.be15_deveagles_be.features.chat.query.service.ChatMessageQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatrooms")
public class ChatMessageQueryController {

  private final ChatMessageQueryService chatMessageQueryService;

  @GetMapping("/{roomId}/messages")
  public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getMessages(
      @PathVariable String roomId) {
    List<ChatMessageResponse> messages = chatMessageQueryService.getMessagesByRoomId(roomId);
    return ResponseEntity.ok(ApiResponse.success(messages));
  }
}
