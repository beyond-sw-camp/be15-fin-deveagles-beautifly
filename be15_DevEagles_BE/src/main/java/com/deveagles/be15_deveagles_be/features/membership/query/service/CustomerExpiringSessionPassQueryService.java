package com.deveagles.be15_deveagles_be.features.membership.query.service;

import com.deveagles.be15_deveagles_be.features.membership.query.dto.request.CustomerExpiringSessionPassFilterRequest;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.CustomerExpiringSessionPassResult;

public interface CustomerExpiringSessionPassQueryService {

  CustomerExpiringSessionPassResult getExpiringSessionPasses(
      Long shopId, CustomerExpiringSessionPassFilterRequest request);
}
