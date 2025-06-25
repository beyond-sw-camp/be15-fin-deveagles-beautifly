package com.deveagles.be15_deveagles_be.features.workflows.command.domain.vo;

import lombok.Getter;

@Getter
public enum TriggerCategory {
  LIFECYCLE("lifecycle", "고객 생애주기 관리", "신규부터 VIP까지 고객 단계별 맞춤 관리"),
  PERIODIC("periodic", "정기 리텐션", "방문 주기와 시술 기반 정기적 재방문 유도"),
  SPECIAL("special", "특별 이벤트", "생일, 기념일 등 특별한 날 기반 마케팅"),
  PREVENTION("prevention", "이탈 방지", "AI 분석 기반 이탈 위험 고객 재활성화");

  private final String code;
  private final String displayName;
  private final String description;

  TriggerCategory(String code, String displayName, String description) {
    this.code = code;
    this.displayName = displayName;
    this.description = description;
  }

  public static TriggerCategory fromCode(String code) {
    for (TriggerCategory category : values()) {
      if (category.code.equals(code)) {
        return category;
      }
    }
    throw new IllegalArgumentException("Unknown trigger category code: " + code);
  }
}
