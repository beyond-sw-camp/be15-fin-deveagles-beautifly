package com.deveagles.be15_deveagles_be.features.workflows.execution.infrastructure.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MockNotificationService implements ActionExecutorServiceImpl.NotificationService {

  @Override
  public boolean sendNotification(
      Long shopId, Long staffId, String title, String content, String level, int targetCount) {
    log.info(
        "Mock 시스템 알림 발송: 매장ID={}, 직원ID={}, 제목={}, 내용={}, 레벨={}, 대상수={}",
        shopId,
        staffId,
        title,
        content,
        level,
        targetCount);

    // 임시로 기본적인 유효성 검증 후 성공 반환
    boolean isValid =
        shopId != null
            && staffId != null
            && title != null
            && !title.trim().isEmpty()
            && content != null
            && !content.trim().isEmpty();

    if (isValid) {
      log.info("시스템 알림 발송 성공");
      return true;
    } else {
      log.warn("시스템 알림 발송 실패: 필수 파라미터 누락");
      return false;
    }
  }
}
