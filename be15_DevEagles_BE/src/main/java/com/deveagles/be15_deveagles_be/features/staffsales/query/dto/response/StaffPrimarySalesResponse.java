package com.deveagles.be15_deveagles_be.features.staffsales.query.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StaffPrimarySalesResponse {

  private Long primaryItemId;
  private String primaryItemName;
  private List<StaffSecondarySalesResponse> secondaryList;
}
