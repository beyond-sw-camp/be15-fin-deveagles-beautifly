package com.deveagles.be15_deveagles_be.features.workflows.command.domain.vo;

import lombok.Getter;

@Getter
public enum ActionType {
  MESSAGE_ONLY("message-only", "메시지만 발송", "선택한 템플릿으로 메시지를 발송합니다"),
  COUPON_MESSAGE("coupon-message", "쿠폰과 메시지 발송", "쿠폰과 함께 메시지를 발송합니다"),
  SYSTEM_NOTIFICATION("system-notification", "시스템 알림", "내부 알림 시스템으로 알림을 발송합니다");

  private final String code;
  private final String displayName;
  private final String description;

  ActionType(String code, String displayName, String description) {
    this.code = code;
    this.displayName = displayName;
    this.description = description;
  }

  public static ActionType fromCode(String code) {
    for (ActionType type : values()) {
      if (type.code.equals(code)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown action type code: " + code);
  }

  public boolean requiresMessageTemplate() {
    return this == MESSAGE_ONLY || this == COUPON_MESSAGE;
  }

  public boolean requiresCoupon() {
    return this == COUPON_MESSAGE;
  }

  public boolean requiresSendTime() {
    return this == MESSAGE_ONLY || this == COUPON_MESSAGE;
  }
}
