package com.deveagles.be15_deveagles_be.features.analytics.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HighRiskCustomer {

  @JsonProperty("customer_id")
  private Long customerId;

  private String name;

  private String phone;

  private String email;

  @JsonProperty("churn_risk_score")
  private Double churnRiskScore;

  @JsonProperty("churn_risk_level")
  private String churnRiskLevel;

  @JsonProperty("total_visits")
  private Integer totalVisits;

  @JsonProperty("total_amount")
  private Double totalAmount;

  @JsonProperty("days_since_last_visit")
  private Integer daysSinceLastVisit;

  private String segment;

  @JsonProperty("last_visit_date")
  private String lastVisitDate;

  @JsonProperty("urgency_score")
  private Double urgencyScore;

  public boolean isCriticalRisk() {
    return "critical".equals(churnRiskLevel);
  }

  public boolean isHighRisk() {
    return "high".equals(churnRiskLevel);
  }

  public boolean requiresImmediateAction() {
    return isCriticalRisk() || (isHighRisk() && urgencyScore > 80);
  }
}
