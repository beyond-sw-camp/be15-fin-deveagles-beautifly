package com.deveagles.be15_deveagles_be.features.workflows.command.application.dto.response;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowCommandResponse {

  private Long workflowId;
  private String title;
  private String description;
  private Long shopId;
  private Long staffId;
  private Boolean isActive;
  private String triggerType;
  private String triggerCategory;
  private String actionType;
  private Long executionCount;
  private Long successCount;
  private Long failureCount;
  private LocalDateTime lastExecutedAt;
  private LocalDateTime nextScheduledAt;
  private LocalDateTime createdAt;
  private LocalDateTime modifiedAt;

  public static WorkflowCommandResponse from(Workflow workflow) {
    return WorkflowCommandResponse.builder()
        .workflowId(workflow.getId())
        .title(workflow.getTitle())
        .description(workflow.getDescription())
        .shopId(workflow.getShopId())
        .staffId(workflow.getStaffId())
        .isActive(workflow.getIsActive())
        .triggerType(workflow.getTriggerType())
        .triggerCategory(workflow.getTriggerCategory())
        .actionType(workflow.getActionType())
        .executionCount(workflow.getExecutionCount())
        .successCount(workflow.getSuccessCount())
        .failureCount(workflow.getFailureCount())
        .lastExecutedAt(workflow.getLastExecutedAt())
        .nextScheduledAt(workflow.getNextScheduledAt())
        .createdAt(workflow.getCreatedAt())
        .modifiedAt(workflow.getModifiedAt())
        .build();
  }
}
