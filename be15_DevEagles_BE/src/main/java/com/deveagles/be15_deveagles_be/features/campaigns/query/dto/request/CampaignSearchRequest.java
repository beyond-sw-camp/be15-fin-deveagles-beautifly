package com.deveagles.be15_deveagles_be.features.campaigns.query.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CampaignSearchRequest {

  @NotNull(message = "상점 ID는 필수입니다.") private Long shopId;

  @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
  private int page = 0;

  @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
  private int size = 10;
}
