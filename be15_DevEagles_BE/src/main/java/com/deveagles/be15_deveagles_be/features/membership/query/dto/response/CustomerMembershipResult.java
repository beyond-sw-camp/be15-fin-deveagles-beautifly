package com.deveagles.be15_deveagles_be.features.membership.query.dto.response;

import com.deveagles.be15_deveagles_be.common.dto.Pagination;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerMembershipResult {
  private List<CustomerMembershipResponse> list;
  private Pagination pagination;
}
