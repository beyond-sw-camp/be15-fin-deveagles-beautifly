package com.deveagles.be15_deveagles_be.features.workflows.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.WorkflowExecution;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.repository.WorkflowExecutionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WorkflowExecutionJpaRepository implements WorkflowExecutionRepository {

  private final WorkflowExecutionJpaDataRepository dataRepository;

  @Override
  public WorkflowExecution save(WorkflowExecution execution) {
    return dataRepository.save(execution);
  }

  @Override
  public Optional<WorkflowExecution> findById(Long id) {
    return dataRepository.findById(id);
  }

  @Override
  public List<WorkflowExecution> findByWorkflowId(Long workflowId) {
    return dataRepository.findByWorkflowId(workflowId);
  }

  @Override
  public List<WorkflowExecution> findByWorkflowIdAndShopId(Long workflowId, Long shopId) {
    return dataRepository.findByWorkflowIdAndShopId(workflowId, shopId);
  }

  @Override
  public List<WorkflowExecution> findByShopId(Long shopId) {
    return dataRepository.findByShopId(shopId);
  }

  @Override
  public List<WorkflowExecution> findByExecutionStatus(String status) {
    return dataRepository.findByExecutionStatus(status);
  }

  @Override
  public List<WorkflowExecution> findRunningExecutions() {
    return dataRepository.findByExecutionStatus("RUNNING");
  }

  @Override
  public List<WorkflowExecution> findExecutionsBetween(
      LocalDateTime startTime, LocalDateTime endTime) {
    return dataRepository.findByCreatedAtBetween(startTime, endTime);
  }

  @Override
  public List<WorkflowExecution> findRecentExecutionsByWorkflowId(Long workflowId, int limit) {
    return dataRepository.findTop10ByWorkflowIdOrderByCreatedAtDesc(workflowId);
  }

  @Override
  public Optional<WorkflowExecution> findLatestByWorkflowId(Long workflowId) {
    return dataRepository.findFirstByWorkflowIdOrderByCreatedAtDesc(workflowId);
  }

  @Override
  public long countByWorkflowId(Long workflowId) {
    return dataRepository.countByWorkflowId(workflowId);
  }

  @Override
  public long countSuccessfulByWorkflowId(Long workflowId) {
    return dataRepository.countByWorkflowIdAndExecutionStatus(workflowId, "SUCCESS");
  }

  @Override
  public long countFailedByWorkflowId(Long workflowId) {
    return dataRepository.countByWorkflowIdAndExecutionStatus(workflowId, "FAILED");
  }

  @Override
  public long countByShopIdAndCreatedAtBetween(
      Long shopId, LocalDateTime start, LocalDateTime end) {
    return dataRepository.countByShopIdAndCreatedAtBetween(shopId, start, end);
  }

  @Override
  public void delete(WorkflowExecution execution) {
    dataRepository.delete(execution);
  }
}
