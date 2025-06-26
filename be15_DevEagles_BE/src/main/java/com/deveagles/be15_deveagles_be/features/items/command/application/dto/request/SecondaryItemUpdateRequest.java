package com.deveagles.be15_deveagles_be.features.items.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class SecondaryItemUpdateRequest {

  @NotNull(message = "1차 상품 ID는 필수입니다.") private Long primaryItemId;

  @NotBlank(message = "2차 상품명은 필수입니다.")
  private String secondaryItemName;

  @NotNull(message = "2차 상품 가격은 필수입니다.") private Integer secondaryItemPrice;

  private Integer timeTaken;

  private boolean isActive;
}
