package com.deveagles.be15_deveagles_be.features.workflows.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateWorkflowCommand {

  @NotBlank(message = "워크플로우 제목은 필수입니다")
  @Size(max = 100, message = "제목은 100자를 초과할 수 없습니다")
  private String title;

  @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다")
  private String description;

  @NotNull(message = "매장 ID는 필수입니다") private Long shopId;

  @NotNull(message = "직원 ID는 필수입니다") private Long staffId;

  // 대상 고객 조건
  private String targetCustomerGrades;
  private String targetTags;
  private Boolean excludeDormantCustomers;
  private Integer dormantPeriodMonths;
  private Boolean excludeRecentMessageReceivers;
  private Integer recentMessagePeriodDays;

  // 트리거 설정
  @NotBlank(message = "트리거 타입은 필수입니다")
  private String triggerType;

  @NotBlank(message = "트리거 카테고리는 필수입니다")
  private String triggerCategory;

  private String triggerConfig;

  // 액션 설정
  @NotBlank(message = "액션 타입은 필수입니다")
  private String actionType;

  @NotBlank(message = "액션 설정은 필수입니다")
  private String actionConfig;

  @Builder.Default private Boolean isActive = true;
}
