package com.deveagles.be15_deveagles_be.features.workflows.command.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TriggerConfig {

  // 방문 주기 기반 (visit-cycle)
  private Integer visitCycleDays;

  // 특정 시술 후 (specific-treatment)
  private String treatmentId;
  private Integer daysAfterTreatment;

  // 고객 등록 후 (customer-registration)
  private Integer daysAfterRegistration;

  // 생일 이벤트 (birthday)
  private Integer birthdayDaysBefore;

  // 방문 횟수 기념 (visit-milestone)
  private Integer visitMilestone;

  // 누적 금액 기념 (amount-milestone)
  private Long amountMilestone;

  // 첫 방문 이후 경과 (first-visit-days-after)
  private Integer daysAfterFirstVisit;

  // 이탈 위험 기반 (risk-based)
  private Integer riskThresholdDays;

  // 신규 고객 팔로업 (new-customer-followup)
  private Integer followupDays;

  // 기타 설정
  private String customCondition;
  private Integer thresholdValue;

  public boolean isValid(TriggerType triggerType) {
    switch (triggerType) {
      case VISIT_CYCLE:
        return visitCycleDays != null && visitCycleDays > 0;
      case SPECIFIC_TREATMENT:
        return treatmentId != null && !treatmentId.trim().isEmpty();
      case BIRTHDAY:
        return birthdayDaysBefore != null && birthdayDaysBefore >= 0;
      case VISIT_MILESTONE:
        return visitMilestone != null && visitMilestone > 0;
      case AMOUNT_MILESTONE:
        return amountMilestone != null && amountMilestone > 0;
      case FIRST_VISIT_DAYS_AFTER:
        return daysAfterFirstVisit != null && daysAfterFirstVisit > 0;
      case NEW_CUSTOMER_FOLLOWUP:
        return followupDays != null && followupDays > 0;
      default:
        return true; // 기본적으로 설정이 필요 없는 트리거들
    }
  }
}
