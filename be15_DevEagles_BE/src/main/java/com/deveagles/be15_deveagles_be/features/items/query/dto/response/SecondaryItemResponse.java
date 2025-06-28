package com.deveagles.be15_deveagles_be.features.items.query.dto.response;

import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.SecondaryItem;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SecondaryItemResponse {

  private Long primaryItemId;
  private Long secondaryItemId;
  private String secondaryItemName;
  private Integer secondaryItemPrice;
  private Integer timeTaken;
  private boolean isActive;

  public static SecondaryItemResponse from(SecondaryItem item) {
    return SecondaryItemResponse.builder()
        .primaryItemId(item.getPrimaryItemId())
        .secondaryItemId(item.getSecondaryItemId())
        .secondaryItemName(item.getSecondaryItemName())
        .secondaryItemPrice(item.getSecondaryItemPrice())
        .timeTaken(item.getTimeTaken())
        .isActive(item.getIsActive())
        .build();
  }
}
