package com.deveagles.be15_deveagles_be.features.workflows.command.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActionConfig {

  // 메시지 발송 관련 (message-only, coupon-message)
  private String messageTemplateId;
  private LocalTime sendTime;

  // 쿠폰 발송 관련 (coupon-message)
  private String couponId;

  // 시스템 알림 관련 (system-notification)
  private String notificationTitle;
  private String notificationContent;
  private String notificationLevel; // INFO, WARNING, ERROR

  // 발송 옵션
  private Boolean sendImmediately;
  private Integer delayMinutes;
  private Integer maxRetryCount;

  // 개인화 설정
  private Boolean usePersonalization;
  private String fallbackMessage;

  public boolean isValid(ActionType actionType) {
    switch (actionType) {
      case MESSAGE_ONLY:
        return messageTemplateId != null && !messageTemplateId.trim().isEmpty() && sendTime != null;
      case COUPON_MESSAGE:
        return messageTemplateId != null
            && !messageTemplateId.trim().isEmpty()
            && couponId != null
            && !couponId.trim().isEmpty()
            && sendTime != null;
      case SYSTEM_NOTIFICATION:
        return notificationTitle != null
            && !notificationTitle.trim().isEmpty()
            && notificationContent != null
            && !notificationContent.trim().isEmpty();
      default:
        return false;
    }
  }

  public boolean requiresScheduling() {
    return sendTime != null || delayMinutes != null;
  }

  public boolean hasRetryConfiguration() {
    return maxRetryCount != null && maxRetryCount > 0;
  }
}
