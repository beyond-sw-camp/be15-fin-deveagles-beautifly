package com.deveagles.be15_deveagles_be.features.workflows.command.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.WorkflowExecution;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.repository.WorkflowExecutionRepository;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.repository.WorkflowRepository;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("워크플로우 실행 서비스 테스트")
class WorkflowExecutionServiceTest {

  @Mock private WorkflowRepository workflowRepository;

  @Mock private WorkflowExecutionRepository workflowExecutionRepository;

  @Mock private CustomerFilterService customerFilterService;

  @Mock private ActionExecutorService actionExecutorService;

  @InjectMocks private WorkflowExecutionService workflowExecutionService;

  private Workflow testWorkflow;
  private WorkflowExecution testExecution;

  @BeforeEach
  void setUp() {
    resetTestWorkflow();

    testExecution =
        WorkflowExecution.builder()
            .id(1L)
            .workflowId(1L)
            .shopId(100L)
            .executionStatus("SCHEDULED")
            .triggerType("visit-cycle")
            .actionType("message-only")
            .targetCount(0)
            .successCount(0)
            .failureCount(0)
            .build();
  }

  private void resetTestWorkflow() {
    testWorkflow =
        Workflow.builder()
            .id(1L)
            .title("테스트 워크플로우")
            .shopId(100L)
            .staffId(10L)
            .isActive(true)
            .triggerType("visit-cycle")
            .actionType("message-only")
            .actionConfig("{\"messageTemplateId\":\"template1\",\"sendTime\":\"14:30\"}")
            .executionCount(0L)
            .successCount(0L)
            .failureCount(0L)
            .build();
  }

  @Test
  @DisplayName("정상적인 워크플로우 실행 - 성공")
  void executeWorkflow_정상실행_성공() {
    // Given
    resetTestWorkflow(); // 테스트마다 새로운 인스턴스
    List<Long> targetCustomerIds = Arrays.asList(1L, 2L, 3L);
    WorkflowExecutionService.ActionExecutionResult successResult =
        new WorkflowExecutionService.ActionExecutionResult(3, 0);

    when(customerFilterService.filterTargetCustomerIds(testWorkflow)).thenReturn(targetCustomerIds);
    when(workflowExecutionRepository.save(any(WorkflowExecution.class))).thenReturn(testExecution);
    when(actionExecutorService.executeAction(eq(testWorkflow), eq(targetCustomerIds), any()))
        .thenReturn(successResult);
    when(workflowRepository.save(any(Workflow.class))).thenReturn(testWorkflow);

    // When
    workflowExecutionService.executeWorkflow(testWorkflow);

    // Then
    verify(customerFilterService).filterTargetCustomerIds(testWorkflow);
    verify(actionExecutorService).executeAction(eq(testWorkflow), eq(targetCustomerIds), any());
    verify(workflowRepository).save(any(Workflow.class));
    verify(workflowExecutionRepository, times(2)).save(any(WorkflowExecution.class));

    // 워크플로우 통계가 업데이트 되었는지 확인
    assertEquals(1L, testWorkflow.getExecutionCount());
    assertEquals(1L, testWorkflow.getSuccessCount());
    assertEquals(0L, testWorkflow.getFailureCount());
  }

  @Test
  @DisplayName("대상 고객이 없는 경우 - 워크플로우 건너뛰기")
  void executeWorkflow_대상고객없음_건너뛰기() {
    // Given
    resetTestWorkflow(); // 테스트마다 새로운 인스턴스
    List<Long> emptyCustomerIds = Collections.emptyList();

    when(customerFilterService.filterTargetCustomerIds(testWorkflow)).thenReturn(emptyCustomerIds);
    when(workflowExecutionRepository.save(any(WorkflowExecution.class))).thenReturn(testExecution);

    // When
    workflowExecutionService.executeWorkflow(testWorkflow);

    // Then
    verify(customerFilterService).filterTargetCustomerIds(testWorkflow);
    verify(actionExecutorService, never()).executeAction(any(), any(), any());
    verify(workflowExecutionRepository, times(2)).save(any(WorkflowExecution.class));

    // 실행 통계는 업데이트 안됨
    assertEquals(0L, testWorkflow.getExecutionCount());
    assertEquals(0L, testWorkflow.getSuccessCount());
  }

  @Test
  @DisplayName("비활성화된 워크플로우 - 실행 안함")
  void executeWorkflow_비활성화된워크플로우_실행안함() {
    // Given
    testWorkflow =
        Workflow.builder()
            .id(1L)
            .isActive(false) // 비활성화
            .build();

    // When
    workflowExecutionService.executeWorkflow(testWorkflow);

    // Then
    verify(customerFilterService, never()).filterTargetCustomerIds(any());
    verify(actionExecutorService, never()).executeAction(any(), any(), any());
    verify(workflowRepository, never()).save(any());
    verify(workflowExecutionRepository, never()).save(any());
  }

  @Test
  @DisplayName("액션 실행 중 예외 발생 - 실패 처리")
  void executeWorkflow_액션실행예외_실패처리() {
    // Given
    resetTestWorkflow(); // 테스트마다 새로운 인스턴스
    List<Long> targetCustomerIds = Arrays.asList(1L, 2L);
    RuntimeException actionException = new RuntimeException("액션 실행 오류");

    when(customerFilterService.filterTargetCustomerIds(testWorkflow)).thenReturn(targetCustomerIds);
    when(workflowExecutionRepository.save(any(WorkflowExecution.class))).thenReturn(testExecution);
    when(actionExecutorService.executeAction(any(), any(), any())).thenThrow(actionException);

    // When
    workflowExecutionService.executeWorkflow(testWorkflow);

    // Then
    verify(customerFilterService).filterTargetCustomerIds(testWorkflow);
    verify(actionExecutorService).executeAction(any(), any(), any());
    verify(workflowRepository).save(any(Workflow.class));
    verify(workflowExecutionRepository, times(2)).save(any(WorkflowExecution.class));

    // 실패로 기록됨
    assertEquals(1L, testWorkflow.getExecutionCount());
    assertEquals(0L, testWorkflow.getSuccessCount());
    assertEquals(1L, testWorkflow.getFailureCount());
  }

  @Test
  @DisplayName("트리거된 워크플로우 실행 - 단일 고객 대상")
  void executeTriggeredWorkflow_단일고객_성공() {
    // Given
    resetTestWorkflow(); // 테스트마다 새로운 인스턴스
    Long triggeredCustomerId = 5L;
    WorkflowExecutionService.ActionExecutionResult successResult =
        new WorkflowExecutionService.ActionExecutionResult(1, 0);

    when(workflowExecutionRepository.save(any(WorkflowExecution.class))).thenReturn(testExecution);
    when(actionExecutorService.executeAction(
            eq(testWorkflow), eq(Arrays.asList(triggeredCustomerId)), any()))
        .thenReturn(successResult);
    when(workflowRepository.save(any(Workflow.class))).thenReturn(testWorkflow);

    // When
    workflowExecutionService.executeTriggeredWorkflow(testWorkflow, triggeredCustomerId);

    // Then
    verify(actionExecutorService)
        .executeAction(eq(testWorkflow), eq(Arrays.asList(triggeredCustomerId)), any());
    verify(workflowRepository).save(any(Workflow.class));
    verify(workflowExecutionRepository, times(2)).save(any(WorkflowExecution.class));

    // CustomerFilterService는 호출되지 않음 (이미 특정 고객 지정됨)
    verify(customerFilterService, never()).filterTargetCustomerIds(any());

    // 통계 업데이트 확인
    assertEquals(1L, testWorkflow.getExecutionCount());
    assertEquals(1L, testWorkflow.getSuccessCount());
  }

  @Test
  @DisplayName("워크플로우 성공률 계산 - 정확한 비율")
  void testWorkflowSuccessRate() {
    // Given
    testWorkflow = Workflow.builder().executionCount(10L).successCount(7L).failureCount(3L).build();

    // When
    double successRate = testWorkflow.getSuccessRate();

    // Then
    assertEquals(70.0, successRate, 0.01);
  }

  @Test
  @DisplayName("실행 통계 업데이트 - 성공 케이스")
  void recordExecution_성공케이스() {
    // Given
    Workflow freshWorkflow =
        Workflow.builder().executionCount(0L).successCount(0L).failureCount(0L).build();
    LocalDateTime beforeExecution = LocalDateTime.now().minusNanos(1);

    // When
    freshWorkflow.recordExecution(true);

    // Then
    assertEquals(1L, freshWorkflow.getExecutionCount());
    assertEquals(1L, freshWorkflow.getSuccessCount());
    assertEquals(0L, freshWorkflow.getFailureCount());
    assertNotNull(freshWorkflow.getLastExecutedAt());
    assertTrue(freshWorkflow.getLastExecutedAt().isAfter(beforeExecution));
  }

  @Test
  @DisplayName("실행 통계 업데이트 - 실패 케이스")
  void recordExecution_실패케이스() {
    // Given
    resetTestWorkflow(); // 각 테스트마다 새로운 인스턴스
    Workflow freshWorkflow =
        Workflow.builder().executionCount(0L).successCount(0L).failureCount(0L).build();

    // When
    freshWorkflow.recordExecution(false);

    // Then
    assertEquals(1L, freshWorkflow.getExecutionCount());
    assertEquals(0L, freshWorkflow.getSuccessCount());
    assertEquals(1L, freshWorkflow.getFailureCount());
  }

  @Test
  @DisplayName("다음 실행 시간 스케줄링 - 방문 주기 기반")
  void scheduleNextExecution_방문주기기반() {
    // Given
    Workflow freshWorkflow = Workflow.builder().build();
    LocalDateTime beforeScheduling = LocalDateTime.now();

    // When
    freshWorkflow.updateSchedule(LocalDateTime.now().plusDays(1));

    // Then
    assertNotNull(freshWorkflow.getNextScheduledAt());
    assertTrue(freshWorkflow.getNextScheduledAt().isAfter(beforeScheduling));
  }

  @Test
  @DisplayName("실행 가능 여부 체크 - 활성화된 워크플로우")
  void canExecute_활성화된워크플로우_true() {
    // Given
    Workflow activeWorkflow = Workflow.builder().isActive(true).deletedAt(null).build();

    // When & Then
    assertTrue(activeWorkflow.canExecute());
  }

  @Test
  @DisplayName("실행 가능 여부 체크 - 삭제된 워크플로우")
  void canExecute_삭제된워크플로우_false() {
    // Given
    Workflow deletedWorkflow =
        Workflow.builder().isActive(true).deletedAt(LocalDateTime.now()).build();

    // When & Then
    assertFalse(deletedWorkflow.canExecute());
  }
}
