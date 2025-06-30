package com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageSettingRequest {

  @Pattern(regexp = "^[0-9]{10,12}$", message = "전화번호는 10~12자리 숫자여야 합니다.")
  private String senderNumber;

  private Boolean canAlimtalk; // null 가능. 기본값 false 처리 가능

  @Min(value = 1, message = "포인트는 음수가 될 수 없습니다.")
  private Long point;
}
