package com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response;

import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResponse {

  private Long tagId;
  private Long shopId;
  private String tagName;
  private String colorCode;

  // 빌더 패턴을 위한 팩터리 메서드
  public static TagResponse from(Tag tag) {
    return TagResponse.builder()
        .tagId(tag.getId())
        .shopId(tag.getShopId())
        .tagName(tag.getTagName())
        .colorCode(tag.getColorCode())
        .build();
  }
}
