package com.deveagles.be15_deveagles_be.features.items.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SecondaryItemRegistRequest {

  @NotNull(message = "1차 상품 ID는 필수입니다.") private Long primaryItemId;

  @NotBlank(message = "2차 상품명을 입력해주세요.")
  private String secondaryItemName;

  @NotNull(message = "상품 금액을 입력해주세요.") private Integer secondaryItemPrice;

  private Integer timeTaken;
}
