package com.deveagles.be15_deveagles_be.features.items.command.application.dto.request;

import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PrimaryItemRequest {

  @NotNull(message = "shopId는 필수입니다.") private Long shopId;

  @NotNull(message = "카테고리를 선택해주세요.") private Category category;

  @NotBlank(message = "1차 상품명을 입력해주세요.")
  private String primaryItemName;
}
