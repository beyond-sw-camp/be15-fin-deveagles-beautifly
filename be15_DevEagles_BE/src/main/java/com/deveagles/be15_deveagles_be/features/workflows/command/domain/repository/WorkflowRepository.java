package com.deveagles.be15_deveagles_be.features.workflows.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkflowRepository {

  Workflow save(Workflow workflow);

  Optional<Workflow> findById(Long id);

  Optional<Workflow> findByIdAndShopId(Long id, Long shopId);

  List<Workflow> findAllByShopId(Long shopId);

  List<Workflow> findAllByShopIdAndIsActiveTrue(Long shopId);

  List<Workflow> findAllByShopIdAndStaffId(Long shopId, Long staffId);

  List<Workflow> findScheduledWorkflows(LocalDateTime currentTime);

  List<Workflow> findByTriggerType(String triggerType);

  List<Workflow> findByTriggerTypeAndShopId(String triggerType, Long shopId);

  boolean existsByTitleAndShopId(String title, Long shopId);

  boolean existsByTitleAndShopIdAndIdNot(String title, Long shopId, Long excludeId);

  void delete(Workflow workflow);

  long countByShopId(Long shopId);

  long countByShopIdAndIsActiveTrue(Long shopId);
}
