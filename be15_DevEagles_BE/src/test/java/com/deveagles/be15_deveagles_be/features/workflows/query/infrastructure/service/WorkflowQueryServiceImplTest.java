package com.deveagles.be15_deveagles_be.features.workflows.query.infrastructure.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.deveagles.be15_deveagles_be.common.dto.PagedResponse;
import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.repository.WorkflowExecutionRepository;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.request.WorkflowSearchRequest;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.response.WorkflowQueryResponse;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.response.WorkflowStatsResponse;
import com.deveagles.be15_deveagles_be.features.workflows.query.application.dto.response.WorkflowSummaryResponse;
import com.deveagles.be15_deveagles_be.features.workflows.query.domain.repository.WorkflowQueryRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("WorkflowQueryService 단위 테스트")
class WorkflowQueryServiceImplTest {

  @Mock private WorkflowQueryRepository workflowQueryRepository;

  @Mock private WorkflowExecutionRepository workflowExecutionRepository;

  @InjectMocks private WorkflowQueryServiceImpl workflowQueryService;

  private Workflow testWorkflow;
  private final Long TEST_SHOP_ID = 1L;
  private final Long TEST_WORKFLOW_ID = 100L;

  @BeforeEach
  void setUp() {
    testWorkflow =
        Workflow.builder()
            .id(TEST_WORKFLOW_ID)
            .title("테스트 워크플로우")
            .description("테스트용 워크플로우입니다")
            .shopId(TEST_SHOP_ID)
            .staffId(1L)
            .isActive(true)
            .targetCustomerGrades("[\"신규 고객\", \"VIP 고객\"]")
            .targetTags("[\"프리미엄\"]")
            .excludeDormantCustomers(true)
            .dormantPeriodMonths(6)
            .excludeRecentMessageReceivers(false)
            .recentMessagePeriodDays(30)
            .triggerType("visit-cycle")
            .triggerCategory("periodic")
            .triggerConfig("{\"visitCycleDays\": 14}")
            .actionType("message-only")
            .actionConfig("{\"messageTemplateId\": \"template1\", \"sendTime\": \"10:00\"}")
            .executionCount(10L)
            .successCount(8L)
            .failureCount(2L)
            .lastExecutedAt(LocalDateTime.now().minusDays(1))
            .nextScheduledAt(LocalDateTime.now().plusDays(1))
            .createdAt(LocalDateTime.now().minusDays(10))
            .modifiedAt(LocalDateTime.now().minusDays(1))
            .build();
  }

  @Nested
  @DisplayName("워크플로우 상세 조회 테스트")
  class GetWorkflowByIdTest {

    @Test
    @DisplayName("정상적으로 워크플로우를 조회한다")
    void getWorkflowById_Success() {
      // given
      given(workflowQueryRepository.findByIdAndShopId(TEST_WORKFLOW_ID, TEST_SHOP_ID))
          .willReturn(Optional.of(testWorkflow));

      // when
      WorkflowQueryResponse result =
          workflowQueryService.getWorkflowById(TEST_WORKFLOW_ID, TEST_SHOP_ID);

      // then
      assertThat(result).isNotNull();
      assertThat(result.getId()).isEqualTo(TEST_WORKFLOW_ID);
      assertThat(result.getTitle()).isEqualTo("테스트 워크플로우");
      assertThat(result.getShopId()).isEqualTo(TEST_SHOP_ID);
      assertThat(result.getIsActive()).isTrue();
      assertThat(result.getTriggerType()).isEqualTo("visit-cycle");
      assertThat(result.getActionType()).isEqualTo("message-only");
      assertThat(result.getSuccessRate()).isEqualTo(80.0);

      verify(workflowQueryRepository).findByIdAndShopId(TEST_WORKFLOW_ID, TEST_SHOP_ID);
    }

    @Test
    @DisplayName("존재하지 않는 워크플로우 조회시 예외가 발생한다")
    void getWorkflowById_NotFound() {
      // given
      given(workflowQueryRepository.findByIdAndShopId(TEST_WORKFLOW_ID, TEST_SHOP_ID))
          .willReturn(Optional.empty());

      // when & then
      assertThatThrownBy(() -> workflowQueryService.getWorkflowById(TEST_WORKFLOW_ID, TEST_SHOP_ID))
          .isInstanceOf(BusinessException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.WORKFLOW_NOT_FOUND);

      verify(workflowQueryRepository).findByIdAndShopId(TEST_WORKFLOW_ID, TEST_SHOP_ID);
    }
  }

  @Nested
  @DisplayName("워크플로우 검색 테스트")
  class SearchWorkflowsTest {

    @Test
    @DisplayName("정상적으로 워크플로우 목록을 검색한다")
    void searchWorkflows_Success() {
      // given
      List<Workflow> workflows = Arrays.asList(testWorkflow);
      Page<Workflow> workflowPage = new PageImpl<>(workflows);

      WorkflowSearchRequest request =
          WorkflowSearchRequest.builder()
              .shopId(TEST_SHOP_ID)
              .searchQuery("테스트")
              .page(0)
              .size(10)
              .sortBy("createdAt")
              .sortDirection("desc")
              .build();

      given(workflowQueryRepository.searchWorkflows(any(WorkflowSearchRequest.class)))
          .willReturn(workflowPage);

      // when
      PagedResponse<WorkflowSummaryResponse> result = workflowQueryService.searchWorkflows(request);

      // then
      assertThat(result).isNotNull();
      assertThat(result.getContent()).hasSize(1);
      assertThat(result.getContent().get(0).getId()).isEqualTo(TEST_WORKFLOW_ID);
      assertThat(result.getContent().get(0).getTitle()).isEqualTo("테스트 워크플로우");

      verify(workflowQueryRepository).searchWorkflows(request);
    }

    @Test
    @DisplayName("빈 결과를 정상적으로 처리한다")
    void searchWorkflows_EmptyResult() {
      // given
      Page<Workflow> emptyPage = new PageImpl<>(List.of());

      WorkflowSearchRequest request =
          WorkflowSearchRequest.builder().shopId(TEST_SHOP_ID).page(0).size(10).build();

      given(workflowQueryRepository.searchWorkflows(any(WorkflowSearchRequest.class)))
          .willReturn(emptyPage);

      // when
      PagedResponse<WorkflowSummaryResponse> result = workflowQueryService.searchWorkflows(request);

      // then
      assertThat(result).isNotNull();
      assertThat(result.getContent()).isEmpty();

      verify(workflowQueryRepository).searchWorkflows(request);
    }
  }

  @Nested
  @DisplayName("워크플로우 통계 조회 테스트")
  class GetWorkflowStatsTest {

    @Test
    @DisplayName("정상적으로 워크플로우 통계를 조회한다")
    void getWorkflowStats_Success() {
      // given
      given(workflowQueryRepository.countByShopId(TEST_SHOP_ID)).willReturn(5L);
      given(workflowQueryRepository.countActiveByShopId(TEST_SHOP_ID)).willReturn(3L);
      given(workflowQueryRepository.countInactiveByShopId(TEST_SHOP_ID)).willReturn(2L);
      given(
              workflowQueryRepository.countExecutionsByShopIdAndPeriod(
                  eq(TEST_SHOP_ID), any(), any()))
          .willReturn(100L);
      given(
              workflowExecutionRepository.countByShopIdAndCreatedAtBetween(
                  eq(TEST_SHOP_ID), any(), any()))
          .willReturn(85L);
      given(workflowQueryRepository.calculateAverageSuccessRateByShopId(TEST_SHOP_ID))
          .willReturn(85.5);

      // when
      WorkflowStatsResponse result = workflowQueryService.getWorkflowStats(TEST_SHOP_ID);

      // then
      assertThat(result).isNotNull();
      assertThat(result.getTotalWorkflows()).isEqualTo(5L);
      assertThat(result.getActiveWorkflows()).isEqualTo(3L);
      assertThat(result.getInactiveWorkflows()).isEqualTo(2L);
      assertThat(result.getMonthlyExecutions()).isEqualTo(100L);
      assertThat(result.getMonthlySuccessfulExecutions()).isEqualTo(85L);
      assertThat(result.getMonthlyFailedExecutions()).isEqualTo(15L);
      assertThat(result.getAverageSuccessRate()).isEqualTo(85.5);

      verify(workflowQueryRepository).countByShopId(TEST_SHOP_ID);
      verify(workflowQueryRepository).countActiveByShopId(TEST_SHOP_ID);
      verify(workflowQueryRepository).countInactiveByShopId(TEST_SHOP_ID);
    }

    @Test
    @DisplayName("평균 성공률이 null인 경우 0.0으로 처리한다")
    void getWorkflowStats_NullAverageSuccessRate() {
      // given
      given(workflowQueryRepository.countByShopId(TEST_SHOP_ID)).willReturn(0L);
      given(workflowQueryRepository.countActiveByShopId(TEST_SHOP_ID)).willReturn(0L);
      given(workflowQueryRepository.countInactiveByShopId(TEST_SHOP_ID)).willReturn(0L);
      given(
              workflowQueryRepository.countExecutionsByShopIdAndPeriod(
                  eq(TEST_SHOP_ID), any(), any()))
          .willReturn(0L);
      given(
              workflowExecutionRepository.countByShopIdAndCreatedAtBetween(
                  eq(TEST_SHOP_ID), any(), any()))
          .willReturn(0L);
      given(workflowQueryRepository.calculateAverageSuccessRateByShopId(TEST_SHOP_ID))
          .willReturn(null);

      // when
      WorkflowStatsResponse result = workflowQueryService.getWorkflowStats(TEST_SHOP_ID);

      // then
      assertThat(result).isNotNull();
      assertThat(result.getAverageSuccessRate()).isEqualTo(0.0);
    }
  }

  @Nested
  @DisplayName("트리거 카테고리별 워크플로우 조회 테스트")
  class GetWorkflowsByTriggerCategoryTest {

    @Test
    @DisplayName("정상적으로 트리거 카테고리별 워크플로우를 조회한다")
    void getWorkflowsByTriggerCategory_Success() {
      // given
      String triggerCategory = "periodic";
      List<Workflow> workflows = Arrays.asList(testWorkflow);

      given(workflowQueryRepository.findByTriggerCategoryAndShopId(triggerCategory, TEST_SHOP_ID))
          .willReturn(workflows);

      // when
      List<WorkflowSummaryResponse> result =
          workflowQueryService.getWorkflowsByTriggerCategory(triggerCategory, TEST_SHOP_ID);

      // then
      assertThat(result).isNotNull();
      assertThat(result).hasSize(1);
      assertThat(result.get(0).getId()).isEqualTo(TEST_WORKFLOW_ID);
      assertThat(result.get(0).getTriggerCategory()).isEqualTo(triggerCategory);

      verify(workflowQueryRepository).findByTriggerCategoryAndShopId(triggerCategory, TEST_SHOP_ID);
    }

    @Test
    @DisplayName("빈 결과를 정상적으로 처리한다")
    void getWorkflowsByTriggerCategory_EmptyResult() {
      // given
      String triggerCategory = "nonexistent";

      given(workflowQueryRepository.findByTriggerCategoryAndShopId(triggerCategory, TEST_SHOP_ID))
          .willReturn(List.of());

      // when
      List<WorkflowSummaryResponse> result =
          workflowQueryService.getWorkflowsByTriggerCategory(triggerCategory, TEST_SHOP_ID);

      // then
      assertThat(result).isNotNull();
      assertThat(result).isEmpty();

      verify(workflowQueryRepository).findByTriggerCategoryAndShopId(triggerCategory, TEST_SHOP_ID);
    }
  }

  @Nested
  @DisplayName("트리거 타입별 워크플로우 조회 테스트")
  class GetWorkflowsByTriggerTypeTest {

    @Test
    @DisplayName("정상적으로 트리거 타입별 워크플로우를 조회한다")
    void getWorkflowsByTriggerType_Success() {
      // given
      String triggerType = "visit-cycle";
      List<Workflow> workflows = Arrays.asList(testWorkflow);

      given(workflowQueryRepository.findByTriggerTypeAndShopId(triggerType, TEST_SHOP_ID))
          .willReturn(workflows);

      // when
      List<WorkflowSummaryResponse> result =
          workflowQueryService.getWorkflowsByTriggerType(triggerType, TEST_SHOP_ID);

      // then
      assertThat(result).isNotNull();
      assertThat(result).hasSize(1);
      assertThat(result.get(0).getId()).isEqualTo(TEST_WORKFLOW_ID);
      assertThat(result.get(0).getTriggerType()).isEqualTo(triggerType);

      verify(workflowQueryRepository).findByTriggerTypeAndShopId(triggerType, TEST_SHOP_ID);
    }
  }

  @Nested
  @DisplayName("최근 워크플로우 조회 테스트")
  class GetRecentWorkflowsTest {

    @Test
    @DisplayName("정상적으로 최근 워크플로우를 조회한다")
    void getRecentWorkflows_Success() {
      // given
      int limit = 5;
      List<Workflow> workflows = Arrays.asList(testWorkflow);

      given(workflowQueryRepository.findRecentWorkflowsByShopId(TEST_SHOP_ID, limit))
          .willReturn(workflows);

      // when
      List<WorkflowSummaryResponse> result =
          workflowQueryService.getRecentWorkflows(TEST_SHOP_ID, limit);

      // then
      assertThat(result).isNotNull();
      assertThat(result).hasSize(1);
      assertThat(result.get(0).getId()).isEqualTo(TEST_WORKFLOW_ID);

      verify(workflowQueryRepository).findRecentWorkflowsByShopId(TEST_SHOP_ID, limit);
    }
  }

  @Nested
  @DisplayName("활성 워크플로우 조회 테스트")
  class GetActiveWorkflowsTest {

    @Test
    @DisplayName("정상적으로 활성 워크플로우를 조회한다")
    void getActiveWorkflows_Success() {
      // given
      List<Workflow> workflows = Arrays.asList(testWorkflow);
      Page<Workflow> workflowPage = new PageImpl<>(workflows);

      given(workflowQueryRepository.searchWorkflows(any(WorkflowSearchRequest.class)))
          .willReturn(workflowPage);

      // when
      List<WorkflowSummaryResponse> result = workflowQueryService.getActiveWorkflows(TEST_SHOP_ID);

      // then
      assertThat(result).isNotNull();
      assertThat(result).hasSize(1);
      assertThat(result.get(0).getId()).isEqualTo(TEST_WORKFLOW_ID);
      assertThat(result.get(0).getIsActive()).isTrue();

      verify(workflowQueryRepository).searchWorkflows(any(WorkflowSearchRequest.class));
    }

    @Test
    @DisplayName("활성 워크플로우가 없는 경우 빈 리스트를 반환한다")
    void getActiveWorkflows_EmptyResult() {
      // given
      Page<Workflow> emptyPage = new PageImpl<>(List.of());

      given(workflowQueryRepository.searchWorkflows(any(WorkflowSearchRequest.class)))
          .willReturn(emptyPage);

      // when
      List<WorkflowSummaryResponse> result = workflowQueryService.getActiveWorkflows(TEST_SHOP_ID);

      // then
      assertThat(result).isNotNull();
      assertThat(result).isEmpty();

      verify(workflowQueryRepository).searchWorkflows(any(WorkflowSearchRequest.class));
    }
  }
}
