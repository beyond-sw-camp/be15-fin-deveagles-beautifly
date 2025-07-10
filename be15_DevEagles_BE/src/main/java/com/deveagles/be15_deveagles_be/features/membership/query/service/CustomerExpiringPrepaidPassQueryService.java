package com.deveagles.be15_deveagles_be.features.membership.query.service;

import com.deveagles.be15_deveagles_be.features.membership.query.dto.request.CustomerExpiringPrepaidPassFilterRequest;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.CustomerExpiringPrepaidPassResult;

public interface CustomerExpiringPrepaidPassQueryService {

  CustomerExpiringPrepaidPassResult getExpiringPrepaidPasses(
      Long shopId, CustomerExpiringPrepaidPassFilterRequest request);
}
