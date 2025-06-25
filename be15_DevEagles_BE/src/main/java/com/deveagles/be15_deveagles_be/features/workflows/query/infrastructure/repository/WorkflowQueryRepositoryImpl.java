package com.deveagles.be15_deveagles_be.features.workflows.query.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import com.deveagles.be15_deveagles_be.features.workflows.command.infrastructure.repository.WorkflowJpaDataRepository;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.request.WorkflowSearchRequest;
import com.deveagles.be15_deveagles_be.features.workflows.query.domain.repository.WorkflowQueryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class WorkflowQueryRepositoryImpl implements WorkflowQueryRepository {

  private final WorkflowJpaDataRepository workflowJpaDataRepository;
  private final EntityManager entityManager;

  @Override
  public Optional<Workflow> findByIdAndShopId(Long workflowId, Long shopId) {
    log.debug("워크플로우 ID로 조회 - workflowId: {}, shopId: {}", workflowId, shopId);
    return workflowJpaDataRepository.findByIdAndShopIdAndDeletedAtIsNull(workflowId, shopId);
  }

  @Override
  public Page<Workflow> searchWorkflows(WorkflowSearchRequest request) {
    log.debug(
        "워크플로우 검색 - shopId: {}, searchQuery: {}", request.getShopId(), request.getSearchQuery());

    Specification<Workflow> spec = createWorkflowSpecification(request);
    Pageable pageable = createPageable(request);

    return workflowJpaDataRepository.findAll(spec, pageable);
  }

  @Override
  public long countByShopId(Long shopId) {
    return workflowJpaDataRepository.countByShopIdAndDeletedAtIsNull(shopId);
  }

  @Override
  public long countActiveByShopId(Long shopId) {
    return workflowJpaDataRepository.countByShopIdAndIsActiveTrueAndDeletedAtIsNull(shopId);
  }

  @Override
  public long countInactiveByShopId(Long shopId) {
    return countByShopId(shopId) - countActiveByShopId(shopId);
  }

  @Override
  public long countExecutionsByShopIdAndPeriod(
      Long shopId, LocalDateTime startDate, LocalDateTime endDate) {
    // WorkflowExecution 테이블에서 실행 횟수를 조회해야 하지만,
    // 현재는 Workflow 테이블의 executionCount 합계로 대체
    String jpql =
        """
            SELECT COALESCE(SUM(w.executionCount), 0)
            FROM Workflow w
            WHERE w.shopId = :shopId
            AND w.deletedAt IS NULL
            AND w.lastExecutedAt BETWEEN :startDate AND :endDate
            """;

    TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
    query.setParameter("shopId", shopId);
    query.setParameter("startDate", startDate);
    query.setParameter("endDate", endDate);

    return query.getSingleResult();
  }

  @Override
  public List<Workflow> findByTriggerCategoryAndShopId(String triggerCategory, Long shopId) {
    return workflowJpaDataRepository.findByTriggerCategoryAndShopIdAndDeletedAtIsNull(
        triggerCategory, shopId);
  }

  @Override
  public List<Workflow> findByTriggerTypeAndShopId(String triggerType, Long shopId) {
    return workflowJpaDataRepository.findByTriggerTypeAndShopIdAndDeletedAtIsNull(
        triggerType, shopId);
  }

  @Override
  public List<Workflow> findRecentWorkflowsByShopId(Long shopId, int limit) {
    Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
    return workflowJpaDataRepository.findByShopIdAndDeletedAtIsNull(shopId, pageable).getContent();
  }

  @Override
  public Double calculateAverageSuccessRateByShopId(Long shopId) {
    String jpql =
        """
            SELECT AVG(
                CASE
                    WHEN w.executionCount > 0 THEN (w.successCount * 100.0 / w.executionCount)
                    ELSE 0.0
                END
            )
            FROM Workflow w
            WHERE w.shopId = :shopId
            AND w.deletedAt IS NULL
            AND w.executionCount > 0
            """;

    TypedQuery<Double> query = entityManager.createQuery(jpql, Double.class);
    query.setParameter("shopId", shopId);

    return query.getSingleResult();
  }

  private Specification<Workflow> createWorkflowSpecification(WorkflowSearchRequest request) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      // Shop ID 필수 조건
      predicates.add(criteriaBuilder.equal(root.get("shopId"), request.getShopId()));

      // 삭제되지 않은 것만
      predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

      // 검색어 조건
      if (request.hasSearchQuery()) {
        String searchPattern = "%" + request.getSearchQuery().toLowerCase() + "%";
        Predicate titlePredicate =
            criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), searchPattern);
        Predicate descriptionPredicate =
            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchPattern);
        predicates.add(criteriaBuilder.or(titlePredicate, descriptionPredicate));
      }

      // 상태 필터
      if (request.hasStatusFilter()) {
        if ("active".equals(request.getStatusFilter())) {
          predicates.add(criteriaBuilder.isTrue(root.get("isActive")));
        } else if ("inactive".equals(request.getStatusFilter())) {
          predicates.add(criteriaBuilder.isFalse(root.get("isActive")));
        }
      }

      // 활성화 상태 필터
      if (request.hasActiveFilter()) {
        predicates.add(criteriaBuilder.equal(root.get("isActive"), request.getIsActive()));
      }

      // 트리거 카테고리 필터
      if (request.hasTriggerCategoryFilter()) {
        predicates.add(
            criteriaBuilder.equal(root.get("triggerCategory"), request.getTriggerCategory()));
      }

      // 트리거 타입 필터
      if (request.hasTriggerTypeFilter()) {
        predicates.add(criteriaBuilder.equal(root.get("triggerType"), request.getTriggerType()));
      }

      // 액션 타입 필터
      if (request.hasActionTypeFilter()) {
        predicates.add(criteriaBuilder.equal(root.get("actionType"), request.getActionType()));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  private Pageable createPageable(WorkflowSearchRequest request) {
    Sort.Direction direction =
        "asc".equalsIgnoreCase(request.getSortDirection())
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;

    Sort sort = Sort.by(direction, request.getSortBy());

    return PageRequest.of(request.getPage(), request.getSize(), sort);
  }
}
