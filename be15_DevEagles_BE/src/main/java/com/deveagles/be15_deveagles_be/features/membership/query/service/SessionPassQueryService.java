package com.deveagles.be15_deveagles_be.features.membership.query.service;

import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.SessionPassResponse;
import java.util.List;

public interface SessionPassQueryService {
  List<SessionPassResponse> getAllSessionPass();
}
