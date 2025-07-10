package com.deveagles.be15_deveagles_be.features.membership.query.service;

import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.PrepaidPassResponse;
import java.util.List;

public interface PrepaidPassQueryService {
  List<PrepaidPassResponse> getAllPrepaidPass(Long shopId);
}
