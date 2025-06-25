package com.deveagles.be15_deveagles_be.features.workflows.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WorkflowJpaDataRepository
    extends JpaRepository<Workflow, Long>, JpaSpecificationExecutor<Workflow> {

  Optional<Workflow> findByIdAndShopIdAndDeletedAtIsNull(Long id, Long shopId);

  List<Workflow> findAllByShopIdAndDeletedAtIsNull(Long shopId);

  List<Workflow> findAllByShopIdAndIsActiveTrueAndDeletedAtIsNull(Long shopId);

  List<Workflow> findAllByShopIdAndStaffIdAndDeletedAtIsNull(Long shopId, Long staffId);

  List<Workflow> findByIsActiveTrueAndDeletedAtIsNullAndNextScheduledAtLessThanEqual(
      LocalDateTime currentTime);

  List<Workflow> findByTriggerTypeAndDeletedAtIsNull(String triggerType);

  List<Workflow> findByTriggerTypeAndShopIdAndDeletedAtIsNull(String triggerType, Long shopId);

  boolean existsByTitleAndShopIdAndDeletedAtIsNull(String title, Long shopId);

  boolean existsByTitleAndShopIdAndIdNotAndDeletedAtIsNull(
      String title, Long shopId, Long excludeId);

  long countByShopIdAndDeletedAtIsNull(Long shopId);

  long countByShopIdAndIsActiveTrueAndDeletedAtIsNull(Long shopId);

  List<Workflow> findByTriggerCategoryAndShopIdAndDeletedAtIsNull(
      String triggerCategory, Long shopId);

  Page<Workflow> findByShopIdAndDeletedAtIsNull(Long shopId, Pageable pageable);
}
