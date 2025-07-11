package com.deveagles.be15_deveagles_be.features.chat.query.controller;

import com.deveagles.be15_deveagles_be.common.dto.ApiResponse;
import com.deveagles.be15_deveagles_be.features.auth.command.application.model.CustomUser;
import com.deveagles.be15_deveagles_be.features.chat.query.dto.response.ChatRoomSummaryResponse;
import com.deveagles.be15_deveagles_be.features.chat.query.service.ChatRoomQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chatrooms")
public class ChatRoomQueryController {

  private final ChatRoomQueryService chatRoomQueryService;

  @GetMapping()
  public ResponseEntity<ApiResponse<List<ChatRoomSummaryResponse>>> getMyChatRooms(
      @AuthenticationPrincipal CustomUser customUser) {
    boolean isStaff = (customUser.getUserId() == 17); // 또는 Role 기반으로 변경 가능
    List<ChatRoomSummaryResponse> result =
        chatRoomQueryService.getMyChatRooms(customUser.getUserId(), isStaff);
    return ResponseEntity.ok(ApiResponse.success(result));
  }
}
