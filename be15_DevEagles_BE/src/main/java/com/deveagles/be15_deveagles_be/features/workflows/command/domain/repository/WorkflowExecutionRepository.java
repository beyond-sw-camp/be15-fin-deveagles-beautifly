package com.deveagles.be15_deveagles_be.features.workflows.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.WorkflowExecution;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkflowExecutionRepository {

  WorkflowExecution save(WorkflowExecution execution);

  Optional<WorkflowExecution> findById(Long id);

  List<WorkflowExecution> findByWorkflowId(Long workflowId);

  List<WorkflowExecution> findByWorkflowIdAndShopId(Long workflowId, Long shopId);

  List<WorkflowExecution> findByShopId(Long shopId);

  List<WorkflowExecution> findByExecutionStatus(String status);

  List<WorkflowExecution> findRunningExecutions();

  List<WorkflowExecution> findExecutionsBetween(LocalDateTime startTime, LocalDateTime endTime);

  List<WorkflowExecution> findRecentExecutionsByWorkflowId(Long workflowId, int limit);

  Optional<WorkflowExecution> findLatestByWorkflowId(Long workflowId);

  long countByWorkflowId(Long workflowId);

  long countSuccessfulByWorkflowId(Long workflowId);

  long countFailedByWorkflowId(Long workflowId);

  long countByShopIdAndCreatedAtBetween(Long shopId, LocalDateTime start, LocalDateTime end);

  void delete(WorkflowExecution execution);
}
