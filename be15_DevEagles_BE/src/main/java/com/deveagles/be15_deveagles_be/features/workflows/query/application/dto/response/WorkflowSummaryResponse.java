package com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.response;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowSummaryResponse {

  private Long id;
  private String title;
  private String description;
  private Long shopId;
  private Long staffId;
  private Boolean isActive;

  // 트리거 정보
  private String triggerType;
  private String triggerCategory;

  // 액션 정보
  private String actionType;

  // 실행 통계
  private Long executionCount;
  private Long successCount;
  private Long failureCount;
  private Double successRate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime lastExecutedAt;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime modifiedAt;

  public static WorkflowSummaryResponse from(Workflow workflow) {
    return WorkflowSummaryResponse.builder()
        .id(workflow.getId())
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
        .successRate(workflow.getSuccessRate())
        .lastExecutedAt(workflow.getLastExecutedAt())
        .createdAt(workflow.getCreatedAt())
        .modifiedAt(workflow.getModifiedAt())
        .build();
  }

  public static List<WorkflowSummaryResponse> from(List<Workflow> workflows) {
    return workflows.stream().map(WorkflowSummaryResponse::from).toList();
  }
}
