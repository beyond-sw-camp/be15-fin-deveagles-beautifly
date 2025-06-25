package com.deveagles.be15_deveagles_be.features.workflows.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.repository.WorkflowRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WorkflowJpaRepository implements WorkflowRepository {

  private final WorkflowJpaDataRepository dataRepository;

  @Override
  public Workflow save(Workflow workflow) {
    return dataRepository.save(workflow);
  }

  @Override
  public Optional<Workflow> findById(Long id) {
    return dataRepository.findById(id);
  }

  @Override
  public Optional<Workflow> findByIdAndShopId(Long id, Long shopId) {
    return dataRepository.findByIdAndShopIdAndDeletedAtIsNull(id, shopId);
  }

  @Override
  public List<Workflow> findAllByShopId(Long shopId) {
    return dataRepository.findAllByShopIdAndDeletedAtIsNull(shopId);
  }

  @Override
  public List<Workflow> findAllByShopIdAndIsActiveTrue(Long shopId) {
    return dataRepository.findAllByShopIdAndIsActiveTrueAndDeletedAtIsNull(shopId);
  }

  @Override
  public List<Workflow> findAllByShopIdAndStaffId(Long shopId, Long staffId) {
    return dataRepository.findAllByShopIdAndStaffIdAndDeletedAtIsNull(shopId, staffId);
  }

  @Override
  public List<Workflow> findScheduledWorkflows(LocalDateTime currentTime) {
    return dataRepository.findByIsActiveTrueAndDeletedAtIsNullAndNextScheduledAtLessThanEqual(
        currentTime);
  }

  @Override
  public List<Workflow> findByTriggerType(String triggerType) {
    return dataRepository.findByTriggerTypeAndDeletedAtIsNull(triggerType);
  }

  @Override
  public List<Workflow> findByTriggerTypeAndShopId(String triggerType, Long shopId) {
    return dataRepository.findByTriggerTypeAndShopIdAndDeletedAtIsNull(triggerType, shopId);
  }

  @Override
  public boolean existsByTitleAndShopId(String title, Long shopId) {
    return dataRepository.existsByTitleAndShopIdAndDeletedAtIsNull(title, shopId);
  }

  @Override
  public boolean existsByTitleAndShopIdAndIdNot(String title, Long shopId, Long excludeId) {
    return dataRepository.existsByTitleAndShopIdAndIdNotAndDeletedAtIsNull(
        title, shopId, excludeId);
  }

  @Override
  public void delete(Workflow workflow) {
    dataRepository.delete(workflow);
  }

  @Override
  public long countByShopId(Long shopId) {
    return dataRepository.countByShopIdAndDeletedAtIsNull(shopId);
  }

  @Override
  public long countByShopIdAndIsActiveTrue(Long shopId) {
    return dataRepository.countByShopIdAndIsActiveTrueAndDeletedAtIsNull(shopId);
  }
}
