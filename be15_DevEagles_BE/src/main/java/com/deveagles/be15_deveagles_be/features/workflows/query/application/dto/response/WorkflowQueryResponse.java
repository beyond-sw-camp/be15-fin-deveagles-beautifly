package com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.response;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class WorkflowQueryResponse {

  private Long id;
  private String title;
  private String description;
  private Long shopId;
  private Long staffId;
  private Boolean isActive;

  // 대상 고객 조건
  private List<String> targetCustomerGrades;
  private List<String> targetTags;
  private Boolean excludeDormantCustomers;
  private Integer dormantPeriodMonths;
  private Boolean excludeRecentMessageReceivers;
  private Integer recentMessagePeriodDays;

  // 트리거 설정
  private String triggerType;
  private String triggerCategory;
  private Object triggerConfig;

  // 액션 설정
  private String actionType;
  private Object actionConfig;

  // 실행 통계
  private Long executionCount;
  private Long successCount;
  private Long failureCount;
  private Double successRate;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime lastExecutedAt;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime nextScheduledAt;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime createdAt;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime modifiedAt;

  public static WorkflowQueryResponse from(Workflow workflow) {
    ObjectMapper objectMapper = new ObjectMapper();

    return WorkflowQueryResponse.builder()
        .id(workflow.getId())
        .title(workflow.getTitle())
        .description(workflow.getDescription())
        .shopId(workflow.getShopId())
        .staffId(workflow.getStaffId())
        .isActive(workflow.getIsActive())
        .targetCustomerGrades(parseJsonToList(workflow.getTargetCustomerGrades(), objectMapper))
        .targetTags(parseJsonToList(workflow.getTargetTags(), objectMapper))
        .excludeDormantCustomers(workflow.getExcludeDormantCustomers())
        .dormantPeriodMonths(workflow.getDormantPeriodMonths())
        .excludeRecentMessageReceivers(workflow.getExcludeRecentMessageReceivers())
        .recentMessagePeriodDays(workflow.getRecentMessagePeriodDays())
        .triggerType(workflow.getTriggerType())
        .triggerCategory(workflow.getTriggerCategory())
        .triggerConfig(parseJsonToObject(workflow.getTriggerConfig(), objectMapper))
        .actionType(workflow.getActionType())
        .actionConfig(parseJsonToObject(workflow.getActionConfig(), objectMapper))
        .executionCount(workflow.getExecutionCount())
        .successCount(workflow.getSuccessCount())
        .failureCount(workflow.getFailureCount())
        .successRate(workflow.getSuccessRate())
        .lastExecutedAt(workflow.getLastExecutedAt())
        .nextScheduledAt(workflow.getNextScheduledAt())
        .createdAt(workflow.getCreatedAt())
        .modifiedAt(workflow.getModifiedAt())
        .build();
  }

  @SuppressWarnings("unchecked")
  private static List<String> parseJsonToList(String json, ObjectMapper objectMapper) {
    if (json == null || json.trim().isEmpty()) {
      return List.of();
    }
    try {
      return objectMapper.readValue(json, List.class);
    } catch (JsonProcessingException e) {
      return List.of();
    }
  }

  private static Object parseJsonToObject(String json, ObjectMapper objectMapper) {
    if (json == null || json.trim().isEmpty()) {
      return null;
    }
    try {
      return objectMapper.readValue(json, Object.class);
    } catch (JsonProcessingException e) {
      return null;
    }
  }
}
