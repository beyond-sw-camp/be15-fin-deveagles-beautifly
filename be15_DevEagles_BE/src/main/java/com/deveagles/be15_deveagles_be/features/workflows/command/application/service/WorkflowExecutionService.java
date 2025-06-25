package com.deveagles.be15_deveagles_be.features.workflows.command.application.service;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.WorkflowExecution;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.repository.WorkflowExecutionRepository;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.repository.WorkflowRepository;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.vo.ExecutionStatus;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class WorkflowExecutionService {

  private final WorkflowRepository workflowRepository;
  private final WorkflowExecutionRepository workflowExecutionRepository;
  private final CustomerFilterService customerFilterService;
  private final ActionExecutorService actionExecutorService;

  public void executeWorkflow(Workflow workflow) {
    log.info("워크플로우 실행 시작: ID={}, 제목={}", workflow.getId(), workflow.getTitle());

    if (!workflow.canExecute()) {
      log.warn(
          "워크플로우 실행 불가능: ID={}, 활성상태={}, 삭제상태={}",
          workflow.getId(),
          workflow.getIsActive(),
          workflow.isDeleted());
      return;
    }

    WorkflowExecution execution = createExecution(workflow);

    try {
      execution.start();
      workflowExecutionRepository.save(execution);

      List<Long> targetCustomerIds = customerFilterService.filterTargetCustomerIds(workflow);

      if (targetCustomerIds.isEmpty()) {
        log.info("대상 고객이 없어 워크플로우 실행을 건너뜁니다: ID={}", workflow.getId());
        execution.complete();
        execution.updateCounts(0, 0);
        workflowExecutionRepository.save(execution);
        return;
      }

      log.info("대상 고객 {}명에 대해 워크플로우 실행: ID={}", targetCustomerIds.size(), workflow.getId());
      execution.setTargetCount(targetCustomerIds.size());

      ActionExecutionResult result =
          actionExecutorService.executeAction(workflow, targetCustomerIds, execution);

      execution.updateCounts(result.getSuccessCount(), result.getFailureCount());
      execution.complete();

      workflow.recordExecution(result.getSuccessCount() > 0);

      scheduleNextExecution(workflow);

      workflowRepository.save(workflow);
      workflowExecutionRepository.save(execution);

      log.info(
          "워크플로우 실행 완료: ID={}, 성공={}, 실패={}",
          workflow.getId(),
          result.getSuccessCount(),
          result.getFailureCount());

    } catch (Exception e) {
      log.error("워크플로우 실행 중 오류 발생: ID={}, 오류={}", workflow.getId(), e.getMessage(), e);
      execution.fail(e.getMessage());
      workflow.recordExecution(false);

      workflowRepository.save(workflow);
      workflowExecutionRepository.save(execution);
    }
  }

  public void executeTriggeredWorkflow(Workflow workflow, Long customerId) {
    log.info("트리거된 워크플로우 실행: ID={}, 고객ID={}", workflow.getId(), customerId);

    if (!workflow.canExecute()) {
      log.warn("워크플로우 실행 불가능: ID={}", workflow.getId());
      return;
    }

    WorkflowExecution execution = createExecution(workflow);

    try {
      execution.start();
      workflowExecutionRepository.save(execution);

      ActionExecutionResult result =
          actionExecutorService.executeAction(workflow, List.of(customerId), execution);

      execution.updateCounts(result.getSuccessCount(), result.getFailureCount());
      execution.complete();
      execution.setTargetCount(1);

      workflow.recordExecution(result.getSuccessCount() > 0);

      workflowRepository.save(workflow);
      workflowExecutionRepository.save(execution);

      log.info("트리거된 워크플로우 실행 완료: ID={}, 성공={}", workflow.getId(), result.getSuccessCount());

    } catch (Exception e) {
      log.error("트리거된 워크플로우 실행 중 오류: ID={}, 오류={}", workflow.getId(), e.getMessage(), e);
      execution.fail(e.getMessage());
      workflow.recordExecution(false);

      workflowRepository.save(workflow);
      workflowExecutionRepository.save(execution);
    }
  }

  private WorkflowExecution createExecution(Workflow workflow) {
    return WorkflowExecution.builder()
        .workflowId(workflow.getId())
        .shopId(workflow.getShopId())
        .executionStatus(ExecutionStatus.SCHEDULED.getCode())
        .triggerType(workflow.getTriggerType())
        .actionType(workflow.getActionType())
        .build();
  }

  private void scheduleNextExecution(Workflow workflow) {
    switch (workflow.getTriggerType()) {
      case "visit-cycle":
        workflow.updateSchedule(LocalDateTime.now().plusDays(1));
        break;
      case "birthday":
        workflow.updateSchedule(LocalDateTime.now().plusDays(1));
        break;
      case "churn-risk-high":
        // 이탈 위험은 일주일 후 재체크
        workflow.updateSchedule(LocalDateTime.now().plusWeeks(1));
        break;
      default:
        // 기본적으로 하루 후
        workflow.updateSchedule(LocalDateTime.now().plusDays(1));
        break;
    }
  }

  public static class ActionExecutionResult {
    private final int successCount;
    private final int failureCount;

    public ActionExecutionResult(int successCount, int failureCount) {
      this.successCount = successCount;
      this.failureCount = failureCount;
    }

    public int getSuccessCount() {
      return successCount;
    }

    public int getFailureCount() {
      return failureCount;
    }
  }
}
