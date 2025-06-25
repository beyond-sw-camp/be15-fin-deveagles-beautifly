package com.deveagles.be15_deveagles_be.features.workflows.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.WorkflowExecution;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowExecutionJpaDataRepository extends JpaRepository<WorkflowExecution, Long> {

  List<WorkflowExecution> findByWorkflowId(Long workflowId);

  List<WorkflowExecution> findByWorkflowIdAndShopId(Long workflowId, Long shopId);

  List<WorkflowExecution> findByShopId(Long shopId);

  List<WorkflowExecution> findByExecutionStatus(String status);

  List<WorkflowExecution> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);

  List<WorkflowExecution> findTop10ByWorkflowIdOrderByCreatedAtDesc(Long workflowId);

  Optional<WorkflowExecution> findFirstByWorkflowIdOrderByCreatedAtDesc(Long workflowId);

  long countByWorkflowId(Long workflowId);

  long countByWorkflowIdAndExecutionStatus(Long workflowId, String status);

  long countByShopIdAndCreatedAtBetween(Long shopId, LocalDateTime start, LocalDateTime end);
}
