package com.deveagles.be15_deveagles_be.features.messages.query.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SmsSearchRequest {

  @NotNull(message = "매장 ID는 필수입니다.") private Long shopId;

  @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
  private int page = 0;

  @Min(value = 1, message = "한 페이지당 표시할 항목 수는 1개 이상이어야 합니다.")
  private int size = 20;
}
