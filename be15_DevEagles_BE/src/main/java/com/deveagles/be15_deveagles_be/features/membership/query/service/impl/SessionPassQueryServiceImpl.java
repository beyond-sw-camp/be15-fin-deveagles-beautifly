package com.deveagles.be15_deveagles_be.features.membership.query.service.impl;

import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.SessionPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.SessionPassRepository;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.SessionPassResponse;
import com.deveagles.be15_deveagles_be.features.membership.query.service.SessionPassQueryService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionPassQueryServiceImpl implements SessionPassQueryService {

  private final SessionPassRepository sessionPassRepository;

  @Override
  public List<SessionPassResponse> getAllSessionPass() {
    List<SessionPass> pass = sessionPassRepository.findAllByDeletedAtIsNull();
    return pass.stream().map(SessionPassResponse::from).collect(Collectors.toList());
  }
}
