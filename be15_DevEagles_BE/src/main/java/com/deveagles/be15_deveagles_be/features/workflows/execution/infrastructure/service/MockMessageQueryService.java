package com.deveagles.be15_deveagles_be.features.workflows.execution.infrastructure.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MockMessageQueryService implements CustomerFilterServiceImpl.MessageQueryService {

  @Override
  public List<Long> excludeRecentMessageReceivers(
      List<Long> customerIds, LocalDateTime recentDate, Long shopId) {
    log.info("Mock 최근 메시지 수신자 제외: 고객수={}, 기준일={}, 매장ID={}", customerIds.size(), recentDate, shopId);

    // 임시로 7의 배수를 최근 메시지 수신자로 간주하고 제외
    List<Long> filtered =
        customerIds.stream().filter(id -> id % 7 != 0).collect(Collectors.toList());

    log.info("최근 메시지 수신자 제외 후: {}명", filtered.size());
    return filtered;
  }
}
