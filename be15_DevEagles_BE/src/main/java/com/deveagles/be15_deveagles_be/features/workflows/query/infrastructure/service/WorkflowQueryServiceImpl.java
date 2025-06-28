package com.deveagles.be15_deveagles_be.features.workflows.query.infrastructure.service;

import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.repository.WorkflowExecutionRepository;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.request.WorkflowSearchRequest;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.response.WorkflowQueryResponse;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.response.WorkflowStatsResponse;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.response.WorkflowSummaryResponse;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.service.WorkflowQueryService;
import com.deveagles.be15_deveagles_be.features.workflows.query.domain.repository.WorkflowQueryRepository;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkflowQueryServiceImpl implements WorkflowQueryService {

  private final WorkflowQueryRepository workflowQueryRepository;
  private final WorkflowExecutionRepository workflowExecutionRepository;

  @Override
  public WorkflowQueryResponse getWorkflowById(Long workflowId, Long shopId) {
    log.debug("워크플로우 상세 조회 시작 - workflowId: {}, shopId: {}", workflowId, shopId);

    Workflow workflow =
        workflowQueryRepository
            .findByIdAndShopId(workflowId, shopId)
            .orElseThrow(() -> new BusinessException(ErrorCode.WORKFLOW_NOT_FOUND));

    WorkflowQueryResponse response = WorkflowQueryResponse.from(workflow);

    log.debug("워크플로우 상세 조회 완료 - workflowId: {}", workflowId);
    return response;
  }

  @Override
  public PagedResponse<WorkflowSummaryResponse> searchWorkflows(WorkflowSearchRequest request) {
    log.debug(
        "워크플로우 목록 검색 시작 - shopId: {}, page: {}, size: {}",
        request.getShopId(),
        request.getPage(),
        request.getSize());

    Page<Workflow> workflowPage = workflowQueryRepository.searchWorkflows(request);

    List<WorkflowSummaryResponse> responses =
        WorkflowSummaryResponse.from(workflowPage.getContent());

    PagedResult<WorkflowSummaryResponse> pagedResult =
        PagedResult.from(workflowPage.map(WorkflowSummaryResponse::from));
    PagedResponse<WorkflowSummaryResponse> pagedResponse =
        new PagedResponse<>(responses, pagedResult.getPagination());

    log.debug(
        "워크플로우 목록 검색 완료 - shopId: {}, totalElements: {}",
        request.getShopId(),
        workflowPage.getTotalElements());

    return pagedResponse;
  }

  @Override
  public WorkflowStatsResponse getWorkflowStats(Long shopId) {
    log.debug("워크플로우 통계 조회 시작 - shopId: {}", shopId);

    // 기본 통계
    long totalWorkflows = workflowQueryRepository.countByShopId(shopId);
    long activeWorkflows = workflowQueryRepository.countActiveByShopId(shopId);
    long inactiveWorkflows = workflowQueryRepository.countInactiveByShopId(shopId);

    // 이번 달 실행 통계
    YearMonth currentMonth = YearMonth.now();
    LocalDateTime monthStart = currentMonth.atDay(1).atStartOfDay();
    LocalDateTime monthEnd = currentMonth.atEndOfMonth().atTime(23, 59, 59);

    long monthlyExecutions =
        workflowQueryRepository.countExecutionsByShopIdAndPeriod(shopId, monthStart, monthEnd);

    // 성공/실패 통계는 WorkflowExecution 테이블에서 조회
    long monthlySuccessfulExecutions =
        workflowExecutionRepository.countByShopIdAndCreatedAtBetween(shopId, monthStart, monthEnd);

    long monthlyFailedExecutions = monthlyExecutions - monthlySuccessfulExecutions;

    // 평균 성공률
    Double averageSuccessRate = workflowQueryRepository.calculateAverageSuccessRateByShopId(shopId);

    WorkflowStatsResponse response =
        WorkflowStatsResponse.of(
            totalWorkflows,
            activeWorkflows,
            inactiveWorkflows,
            monthlyExecutions,
            monthlySuccessfulExecutions,
            monthlyFailedExecutions,
            averageSuccessRate != null ? averageSuccessRate : 0.0);

    log.debug("워크플로우 통계 조회 완료 - shopId: {}, totalWorkflows: {}", shopId, totalWorkflows);
    return response;
  }

  @Override
  public List<WorkflowSummaryResponse> getWorkflowsByTriggerCategory(
      String triggerCategory, Long shopId) {
    log.debug("트리거 카테고리별 워크플로우 조회 시작 - triggerCategory: {}, shopId: {}", triggerCategory, shopId);

    List<Workflow> workflows =
        workflowQueryRepository.findByTriggerCategoryAndShopId(triggerCategory, shopId);

    List<WorkflowSummaryResponse> responses = WorkflowSummaryResponse.from(workflows);

    log.debug(
        "트리거 카테고리별 워크플로우 조회 완료 - triggerCategory: {}, count: {}",
        triggerCategory,
        workflows.size());

    return responses;
  }

  @Override
  public List<WorkflowSummaryResponse> getWorkflowsByTriggerType(String triggerType, Long shopId) {
    log.debug("트리거 타입별 워크플로우 조회 시작 - triggerType: {}, shopId: {}", triggerType, shopId);

    List<Workflow> workflows =
        workflowQueryRepository.findByTriggerTypeAndShopId(triggerType, shopId);

    List<WorkflowSummaryResponse> responses = WorkflowSummaryResponse.from(workflows);

    log.debug("트리거 타입별 워크플로우 조회 완료 - triggerType: {}, count: {}", triggerType, workflows.size());

    return responses;
  }

  @Override
  public List<WorkflowSummaryResponse> getRecentWorkflows(Long shopId, int limit) {
    log.debug("최근 워크플로우 조회 시작 - shopId: {}, limit: {}", shopId, limit);

    List<Workflow> workflows = workflowQueryRepository.findRecentWorkflowsByShopId(shopId, limit);

    List<WorkflowSummaryResponse> responses = WorkflowSummaryResponse.from(workflows);

    log.debug("최근 워크플로우 조회 완료 - shopId: {}, count: {}", shopId, workflows.size());

    return responses;
  }

  @Override
  public List<WorkflowSummaryResponse> getActiveWorkflows(Long shopId) {
    log.debug("활성 워크플로우 조회 시작 - shopId: {}", shopId);

    WorkflowSearchRequest request =
        WorkflowSearchRequest.builder()
            .shopId(shopId)
            .isActive(true)
            .page(0)
            .size(1000) // 충분히 큰 값으로 설정
            .sortBy("createdAt")
            .sortDirection("desc")
            .build();

    Page<Workflow> workflowPage = workflowQueryRepository.searchWorkflows(request);

    List<WorkflowSummaryResponse> responses =
        WorkflowSummaryResponse.from(workflowPage.getContent());

    log.debug("활성 워크플로우 조회 완료 - shopId: {}, count: {}", shopId, responses.size());

    return responses;
  }
}
