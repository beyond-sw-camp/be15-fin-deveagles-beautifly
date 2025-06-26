package com.deveagles.be15_deveagles_be.features.workflows.execution.infrastructure.service;

import java.time.LocalTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// TODO: 나중에 지울 파일임

@Service
@Slf4j
public class MockMessageService implements ActionExecutorServiceImpl.MessageService {

  @Override
  public boolean sendMessage(Long customerId, Long shopId, String templateId, LocalTime sendTime) {
    log.info(
        "Mock 메시지 발송: 고객ID={}, 매장ID={}, 템플릿ID={}, 발송시간={}",
        customerId,
        shopId,
        templateId,
        sendTime);
    // 임시로 항상 성공 반환 (90% 성공률 시뮬레이션)
    return Math.random() > 0.1;
  }

  @Override
  public boolean sendCouponMessage(
      Long customerId, Long shopId, String templateId, String couponId, LocalTime sendTime) {
    log.info(
        "Mock 쿠폰 메시지 발송: 고객ID={}, 매장ID={}, 템플릿ID={}, 쿠폰ID={}, 발송시간={}",
        customerId,
        shopId,
        templateId,
        couponId,
        sendTime);
    // 임시로 항상 성공 반환 (85% 성공률 시뮬레이션)
    return Math.random() > 0.15;
  }
}
