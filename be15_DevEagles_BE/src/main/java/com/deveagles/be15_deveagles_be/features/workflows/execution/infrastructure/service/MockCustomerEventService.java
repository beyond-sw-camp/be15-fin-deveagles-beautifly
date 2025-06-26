package com.deveagles.be15_deveagles_be.features.workflows.execution.infrastructure.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// TODO: 나중에 지울 파일임

@Service
@Slf4j
public class MockCustomerEventService implements TriggerCheckServiceImpl.CustomerEventService {

  @Override
  public boolean checkVisitCycle(Long customerId, Long shopId, Integer cycleDays) {
    log.info("Mock 방문 주기 체크: 고객ID={}, 매장ID={}, 주기={}일", customerId, shopId, cycleDays);

    // 임시로 고객 ID가 홀수이고 주기가 30일 이하면 트리거되는 것으로 시뮬레이션
    boolean shouldTrigger = customerId % 2 == 1 && (cycleDays != null && cycleDays <= 30);

    log.info("방문 주기 트리거 결과: {}", shouldTrigger);
    return shouldTrigger;
  }

  @Override
  public int getTotalVisitCount(Long customerId, Long shopId) {
    log.info("Mock 총 방문 횟수 조회: 고객ID={}, 매장ID={}", customerId, shopId);

    // 임시로 고객 ID를 기반으로 방문 횟수 시뮬레이션 (1~20회)
    int visitCount = (int) (customerId % 20) + 1;

    log.info("총 방문 횟수: {}회", visitCount);
    return visitCount;
  }

  @Override
  public long getTotalPaymentAmount(Long customerId, Long shopId) {
    log.info("Mock 총 결제 금액 조회: 고객ID={}, 매장ID={}", customerId, shopId);

    // 임시로 고객 ID를 기반으로 누적 결제 금액 시뮬레이션 (10만원~200만원)
    long totalAmount = (customerId * 50000) + 100000;

    log.info("총 결제 금액: {}원", totalAmount);
    return totalAmount;
  }
}
