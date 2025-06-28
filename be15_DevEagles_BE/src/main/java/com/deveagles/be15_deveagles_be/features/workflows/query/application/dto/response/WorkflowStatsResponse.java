package com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowStatsResponse {

  private Long totalWorkflows;
  private Long activeWorkflows;
  private Long inactiveWorkflows;
  private Long monthlyExecutions;
  private Long monthlySuccessfulExecutions;
  private Long monthlyFailedExecutions;
  private Double averageSuccessRate;

  public static WorkflowStatsResponse of(
      Long totalWorkflows,
      Long activeWorkflows,
      Long inactiveWorkflows,
      Long monthlyExecutions,
      Long monthlySuccessfulExecutions,
      Long monthlyFailedExecutions,
      Double averageSuccessRate) {

    return WorkflowStatsResponse.builder()
        .totalWorkflows(totalWorkflows)
        .activeWorkflows(activeWorkflows)
        .inactiveWorkflows(inactiveWorkflows)
        .monthlyExecutions(monthlyExecutions)
        .monthlySuccessfulExecutions(monthlySuccessfulExecutions)
        .monthlyFailedExecutions(monthlyFailedExecutions)
        .averageSuccessRate(averageSuccessRate)
        .build();
  }
}
