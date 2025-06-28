package com.deveagles.be15_deveagles_be.features.workflows.query.domain.repository;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.request.WorkflowSearchRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface WorkflowQueryRepository {

  Optional<Workflow> findByIdAndShopId(Long workflowId, Long shopId);

  Page<Workflow> searchWorkflows(WorkflowSearchRequest request);

  long countByShopId(Long shopId);

  long countActiveByShopId(Long shopId);

  long countInactiveByShopId(Long shopId);

  long countExecutionsByShopIdAndPeriod(
      Long shopId, LocalDateTime startDate, LocalDateTime endDate);

  List<Workflow> findByTriggerCategoryAndShopId(String triggerCategory, Long shopId);

  List<Workflow> findByTriggerTypeAndShopId(String triggerType, Long shopId);

  List<Workflow> findRecentWorkflowsByShopId(Long shopId, int limit);

  Double calculateAverageSuccessRateByShopId(Long shopId);
}
