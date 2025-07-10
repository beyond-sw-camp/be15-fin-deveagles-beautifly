package com.deveagles.be15_deveagles_be.features.membership.query.service;

import com.deveagles.be15_deveagles_be.features.membership.query.dto.request.CustomerMemebershipFilterRequest;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.CustomerExpiringMembershipResult;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.CustomerMembershipResult;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.CustomerPrepaidPassDetailInfo;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.CustomerSessionPassDetailInfo;
import java.util.List;

public interface CustomerMembershipQueryService {

  CustomerMembershipResult getCustomerMembershipList(Long shopId, int page, int size);

  CustomerMembershipResult getCustomerMembershipList(
      Long shopId, CustomerMemebershipFilterRequest filter);

  List<CustomerPrepaidPassDetailInfo> getPrepaidPassDetailsByCustomerId(Long customerId);

  List<CustomerSessionPassDetailInfo> getSessionPassDetailsByCustomerId(Long customerId);

  CustomerExpiringMembershipResult getExpiredOrUsedUpMemberships(Long customerId);
}
