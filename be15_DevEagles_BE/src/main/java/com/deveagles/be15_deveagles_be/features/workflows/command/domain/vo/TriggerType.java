package com.deveagles.be15_deveagles_be.features.workflows.command.domain.vo;

import lombok.Getter;

@Getter
public enum TriggerType {
  // 고객 생애주기 관리
  NEW_CUSTOMER_FOLLOWUP("new-customer-followup", "신규 고객 팔로업", TriggerCategory.LIFECYCLE),
  VIP_ATTENTION_NEEDED("vip-attention-needed", "VIP 고객 특별 관리", TriggerCategory.LIFECYCLE),

  // 정기 리텐션
  VISIT_CYCLE("visit-cycle", "방문 주기 기반", TriggerCategory.PERIODIC),
  SPECIFIC_TREATMENT("specific-treatment", "특정 시술 후", TriggerCategory.PERIODIC),

  // 특별 이벤트
  BIRTHDAY("birthday", "생일 이벤트", TriggerCategory.SPECIAL),
  FIRST_VISIT_ANNIVERSARY("first-visit-anniversary", "첫 방문 기념일", TriggerCategory.SPECIAL),
  VISIT_MILESTONE("visit-milestone", "방문 횟수 기념", TriggerCategory.SPECIAL),
  AMOUNT_MILESTONE("amount-milestone", "누적 금액 기념", TriggerCategory.SPECIAL),
  FIRST_VISIT_DAYS_AFTER("first-visit-days-after", "첫 방문 이후 경과", TriggerCategory.SPECIAL),

  // 이탈 방지
  NEW_CUSTOMER_AT_RISK("new-customer-at-risk", "신규 고객 이탈 위험", TriggerCategory.PREVENTION),
  REACTIVATION_NEEDED("reactivation-needed", "재활성화 필요", TriggerCategory.PREVENTION),
  GROWING_DELAYED("growing-delayed", "성장 고객 케어", TriggerCategory.PREVENTION),
  LOYAL_DELAYED("loyal-delayed", "충성 고객 케어", TriggerCategory.PREVENTION),
  CHURN_RISK_HIGH("churn-risk-high", "고위험 이탈 예측", TriggerCategory.PREVENTION);

  private final String code;
  private final String displayName;
  private final TriggerCategory category;

  TriggerType(String code, String displayName, TriggerCategory category) {
    this.code = code;
    this.displayName = displayName;
    this.category = category;
  }

  public static TriggerType fromCode(String code) {
    for (TriggerType type : values()) {
      if (type.code.equals(code)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown trigger type code: " + code);
  }

  public boolean isPeriodicType() {
    return category == TriggerCategory.PERIODIC;
  }

  public boolean isEventBasedType() {
    return category == TriggerCategory.SPECIAL;
  }

  public boolean isPreventionType() {
    return category == TriggerCategory.PREVENTION;
  }
}
