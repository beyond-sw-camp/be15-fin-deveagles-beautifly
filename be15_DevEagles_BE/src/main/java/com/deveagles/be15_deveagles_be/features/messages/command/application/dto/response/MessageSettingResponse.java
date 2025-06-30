package com.deveagles.be15_deveagles_be.features.messages.command.application.dto.response;

import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageSettings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageSettingResponse {

  private String senderNumber;
  private boolean canAlimtalk;
  private Long point;

  public static MessageSettingResponse from(MessageSettings entity) {
    return MessageSettingResponse.builder()
        .senderNumber(entity.getSenderNumber())
        .canAlimtalk(entity.isCanAlimtalk())
        .point(entity.getPoint())
        .build();
  }
}
