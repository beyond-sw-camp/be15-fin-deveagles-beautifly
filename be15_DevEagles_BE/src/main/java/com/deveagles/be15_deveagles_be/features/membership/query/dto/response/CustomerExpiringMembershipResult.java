package com.deveagles.be15_deveagles_be.features.membership.query.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerExpiringMembershipResult {
  private List<CustomerPrepaidPassDetailInfo> Plist;
  private List<CustomerSessionPassDetailInfo> SList;
}
