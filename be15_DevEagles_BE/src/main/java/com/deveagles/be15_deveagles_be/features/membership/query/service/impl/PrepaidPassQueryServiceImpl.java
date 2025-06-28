package com.deveagles.be15_deveagles_be.features.membership.query.service.impl;

import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.PrepaidPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.PrepaidPassRepository;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.PrepaidPassResponse;
import com.deveagles.be15_deveagles_be.features.membership.query.service.PrepaidPassQueryService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrepaidPassQueryServiceImpl implements PrepaidPassQueryService {

  private final PrepaidPassRepository prepaidPassRepository;

  @Override
  public List<PrepaidPassResponse> getAllPrepaidPass() {
    List<PrepaidPass> pass = prepaidPassRepository.findAllByDeletedAtIsNull();
    return pass.stream().map(PrepaidPassResponse::from).collect(Collectors.toList());
  }
}
