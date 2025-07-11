package com.deveagles.be15_deveagles_be.features.chat.command.application.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.chat.command.application.dto.response.ChatRoomCreateResponse;
import com.deveagles.be15_deveagles_be.features.chat.command.application.dto.response.ChatRoomCreateResult;
import com.deveagles.be15_deveagles_be.features.chat.command.application.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatrooms")
public class ChatRoomController {

  private final ChatRoomService chatRoomService;

  // 1️⃣ 채팅방 생성 API (greeting 메시지는 여기서 보내지 않음)
  @PostMapping
  public ResponseEntity<ApiResponse<ChatRoomCreateResponse>> createRoom(
      @AuthenticationPrincipal CustomUser customUser) {

    Long shopId = customUser.getShopId();
    Long staffId = customUser.getUserId();
    String staffName = customUser.getStaffName();

    ChatRoomCreateResult result = chatRoomService.createChatRoom(shopId, staffId, staffName);

    return ResponseEntity.ok(ApiResponse.success(new ChatRoomCreateResponse(result.roomId())));
  }

  // 2️⃣ 구독 완료 후 greeting 메시지를 전송하는 API
  @PostMapping("/{roomId}/send-greeting")
  public ResponseEntity<Void> sendGreeting(@PathVariable String roomId) {
    chatRoomService.sendGreeting(roomId); // 👈 Impl 호출
    return ResponseEntity.ok().build();
  }

  @PostMapping("/{roomId}/switch-to-staff")
  public ResponseEntity<ApiResponse<Void>> switchToStaff(@PathVariable String roomId) {
    chatRoomService.switchToStaff(roomId); // ✅ 호출
    return ResponseEntity.ok(ApiResponse.success(null));
  }
}
