package com.deveagles.be15_deveagles_be.features.membership.query.service;

import com.deveagles.be15_deveagles_be.features.membership.query.dto.request.CustomerMemebershipFilterRequest;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.CustomerMembershipResult;

public interface CustomerMembershipQueryService {

  CustomerMembershipResult getCustomerMembershipList(Long shopId, int page, int size);

  CustomerMembershipResult getCustomerMembershipList(
      Long shopId, CustomerMemebershipFilterRequest filter);
}
