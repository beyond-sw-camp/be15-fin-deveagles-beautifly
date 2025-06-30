package com.deveagles.be15_deveagles_be.features.membership.command.application.service;

import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.SessionPassRequest;

public interface SessionPassCommandService {
  void registSessionPass(SessionPassRequest request);

  void updateSessionPass(Long sessionPassId, SessionPassRequest request);
}
