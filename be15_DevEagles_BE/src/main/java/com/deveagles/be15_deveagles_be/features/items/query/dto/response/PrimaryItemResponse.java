package com.deveagles.be15_deveagles_be.features.items.query.dto.response;

import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.Category;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.PrimaryItem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PrimaryItemResponse {
  private Long primaryItemId;
  private String primaryItemName;
  private Category category;

  public static PrimaryItemResponse from(PrimaryItem item) {
    return PrimaryItemResponse.builder()
        .primaryItemId(item.getPrimaryItemId())
        .primaryItemName(item.getPrimaryItemName())
        .category(item.getCategory())
        .build();
  }
}
