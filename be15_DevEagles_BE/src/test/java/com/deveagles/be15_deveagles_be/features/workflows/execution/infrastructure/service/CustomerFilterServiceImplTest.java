package com.deveagles.be15_deveagles_be.features.workflows.execution.infrastructure.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("고객 필터링 서비스 구현체 테스트")
class CustomerFilterServiceImplTest {

  @Mock private ObjectMapper objectMapper;
  @Mock private MockCustomerQueryService customerQueryService;
  @Mock private MockMessageQueryService messageQueryService;

  @InjectMocks private CustomerFilterServiceImpl customerFilterService;

  private Workflow testWorkflow;
  private List<Long> candidateCustomerIds;

  @BeforeEach
  void setUp() {
    testWorkflow =
        Workflow.builder()
            .id(1L)
            .shopId(100L)
            .targetCustomerGrades("[1,2,3]")
            .targetTags("[10,20]")
            .excludeDormantCustomers(true)
            .dormantPeriodMonths(6)
            .excludeRecentMessageReceivers(true)
            .recentMessagePeriodDays(7)
            .build();

    candidateCustomerIds = Arrays.asList(1L, 2L, 3L, 4L, 5L);
  }

  @Test
  @DisplayName("전체 필터링 프로세스 - 정상 동작")
  void filterTargetCustomerIds_전체프로세스_정상동작() throws JsonProcessingException {
    // Given
    List<Long> afterGradeFilter = Arrays.asList(1L, 2L, 3L);
    List<Long> afterTagFilter = Arrays.asList(1L, 2L);
    List<Long> afterDormantFilter = Arrays.asList(1L, 2L);
    List<Long> finalResult = Arrays.asList(1L);

    when(customerQueryService.findCustomerIdsByShopId(100L)).thenReturn(candidateCustomerIds);
    when(objectMapper.readValue(eq("[1,2,3]"), any(TypeReference.class)))
        .thenReturn(Arrays.asList(1L, 2L, 3L));
    when(objectMapper.readValue(eq("[10,20]"), any(TypeReference.class)))
        .thenReturn(Arrays.asList(10L, 20L));
    when(customerQueryService.filterCustomersByGradeIds(
            candidateCustomerIds, Arrays.asList(1L, 2L, 3L)))
        .thenReturn(afterGradeFilter);
    when(customerQueryService.filterCustomersByTagIds(afterGradeFilter, Arrays.asList(10L, 20L)))
        .thenReturn(afterTagFilter);
    when(customerQueryService.excludeDormantCustomers(
            eq(afterTagFilter), any(LocalDateTime.class), eq(100L)))
        .thenReturn(afterDormantFilter);
    when(messageQueryService.excludeRecentMessageReceivers(
            eq(afterDormantFilter), any(LocalDateTime.class), eq(100L)))
        .thenReturn(finalResult);

    // When
    List<Long> result = customerFilterService.filterTargetCustomerIds(testWorkflow);

    // Then
    assertEquals(finalResult, result);
    verify(customerQueryService).findCustomerIdsByShopId(100L);
    verify(customerQueryService).filterCustomersByGradeIds(any(), any());
    verify(customerQueryService).filterCustomersByTagIds(any(), any());
    verify(customerQueryService).excludeDormantCustomers(any(), any(), eq(100L));
    verify(messageQueryService).excludeRecentMessageReceivers(any(), any(), eq(100L));
  }

  @Test
  @DisplayName("매장에 고객이 없는 경우 - 빈 리스트 반환")
  void filterTargetCustomerIds_매장고객없음_빈리스트반환() {
    // Given
    when(customerQueryService.findCustomerIdsByShopId(100L)).thenReturn(Collections.emptyList());

    // When
    List<Long> result = customerFilterService.filterTargetCustomerIds(testWorkflow);

    // Then
    assertTrue(result.isEmpty());
    verify(customerQueryService, only()).findCustomerIdsByShopId(100L);
  }

  @Test
  @DisplayName("고객 등급 필터링 - 빈 JSON인 경우 필터링 안함")
  void filterByCustomerGrades_빈JSON_필터링안함() {
    // Given
    testWorkflow = Workflow.builder().shopId(100L).targetCustomerGrades("").build();
    when(customerQueryService.findCustomerIdsByShopId(100L)).thenReturn(candidateCustomerIds);

    // When
    List<Long> result = customerFilterService.filterTargetCustomerIds(testWorkflow);

    // Then
    assertEquals(candidateCustomerIds, result);
    verify(customerQueryService, never()).filterCustomersByGradeIds(any(), any());
  }

  @Test
  @DisplayName("고객 태그 필터링 - null인 경우 필터링 안함")
  void filterByCustomerTags_null_필터링안함() {
    // Given
    testWorkflow = Workflow.builder().shopId(100L).targetTags(null).build();
    when(customerQueryService.findCustomerIdsByShopId(100L)).thenReturn(candidateCustomerIds);

    // When
    List<Long> result = customerFilterService.filterTargetCustomerIds(testWorkflow);

    // Then
    assertEquals(candidateCustomerIds, result);
    verify(customerQueryService, never()).filterCustomersByTagIds(any(), any());
  }

  @Test
  @DisplayName("휴면 고객 제외 - 설정이 false인 경우 제외 안함")
  void excludeDormantCustomers_설정false_제외안함() {
    // Given
    testWorkflow = Workflow.builder().shopId(100L).excludeDormantCustomers(false).build();
    when(customerQueryService.findCustomerIdsByShopId(100L)).thenReturn(candidateCustomerIds);

    // When
    List<Long> result = customerFilterService.filterTargetCustomerIds(testWorkflow);

    // Then
    assertEquals(candidateCustomerIds, result);
    verify(customerQueryService, never()).excludeDormantCustomers(any(), any(), any());
  }

  @Test
  @DisplayName("최근 메시지 수신자 제외 - 설정이 false인 경우 제외 안함")
  void excludeRecentMessageReceivers_설정false_제외안함() {
    // Given
    testWorkflow = Workflow.builder().shopId(100L).excludeRecentMessageReceivers(false).build();
    when(customerQueryService.findCustomerIdsByShopId(100L)).thenReturn(candidateCustomerIds);

    // When
    List<Long> result = customerFilterService.filterTargetCustomerIds(testWorkflow);

    // Then
    assertEquals(candidateCustomerIds, result);
    verify(messageQueryService, never()).excludeRecentMessageReceivers(any(), any(), any());
  }

  @Test
  @DisplayName("JSON 파싱 오류 - 원본 리스트 반환")
  void filterByCustomerGrades_JSON파싱오류_원본리스트반환() throws JsonProcessingException {
    // Given
    testWorkflow = Workflow.builder().shopId(100L).targetCustomerGrades("[invalid json]").build();
    when(customerQueryService.findCustomerIdsByShopId(100L)).thenReturn(candidateCustomerIds);
    when(objectMapper.readValue(eq("[invalid json]"), any(TypeReference.class)))
        .thenThrow(new JsonProcessingException("Invalid JSON") {});

    // When
    List<Long> result = customerFilterService.filterTargetCustomerIds(testWorkflow);

    // Then
    assertEquals(candidateCustomerIds, result);
    verify(customerQueryService, never()).filterCustomersByGradeIds(any(), any());
  }

  @Test
  @DisplayName("트리거 조건 필터링 - 생일 트리거")
  void filterByTriggerConditions_생일트리거() {
    // Given
    testWorkflow = Workflow.builder().triggerType("birthday").build();
    List<Long> birthdayCustomers = Arrays.asList(1L, 3L);
    when(customerQueryService.filterTodayBirthdayCustomers(candidateCustomerIds))
        .thenReturn(birthdayCustomers);

    // When
    List<Long> result =
        customerFilterService.filterByTriggerConditions(candidateCustomerIds, testWorkflow);

    // Then
    assertEquals(birthdayCustomers, result);
    verify(customerQueryService).filterTodayBirthdayCustomers(candidateCustomerIds);
  }

  @Test
  @DisplayName("트리거 조건 필터링 - 방문 주기 트리거")
  void filterByTriggerConditions_방문주기트리거() {
    // Given
    testWorkflow =
        Workflow.builder().triggerType("visit-cycle").triggerConfig("{\"cycleDays\":30}").build();
    List<Long> visitCycleCustomers = Arrays.asList(2L, 4L);
    when(customerQueryService.filterVisitCycleCustomers(candidateCustomerIds, "{\"cycleDays\":30}"))
        .thenReturn(visitCycleCustomers);

    // When
    List<Long> result =
        customerFilterService.filterByTriggerConditions(candidateCustomerIds, testWorkflow);

    // Then
    assertEquals(visitCycleCustomers, result);
    verify(customerQueryService)
        .filterVisitCycleCustomers(candidateCustomerIds, "{\"cycleDays\":30}");
  }

  @Test
  @DisplayName("트리거 조건 필터링 - 기념일 트리거")
  void filterByTriggerConditions_기념일트리거() {
    // Given
    testWorkflow = Workflow.builder().triggerType("first-visit-anniversary").build();
    List<Long> anniversaryCustomers = Arrays.asList(1L, 5L);
    when(customerQueryService.filterAnniversaryCustomers(candidateCustomerIds))
        .thenReturn(anniversaryCustomers);

    // When
    List<Long> result =
        customerFilterService.filterByTriggerConditions(candidateCustomerIds, testWorkflow);

    // Then
    assertEquals(anniversaryCustomers, result);
    verify(customerQueryService).filterAnniversaryCustomers(candidateCustomerIds);
  }

  @Test
  @DisplayName("트리거 조건 필터링 - 이탈 위험 트리거")
  void filterByTriggerConditions_이탈위험트리거() {
    // Given
    testWorkflow = Workflow.builder().triggerType("churn-risk-high").build();
    List<Long> churnRiskCustomers = Arrays.asList(3L, 4L);
    when(customerQueryService.filterHighChurnRiskCustomers(candidateCustomerIds))
        .thenReturn(churnRiskCustomers);

    // When
    List<Long> result =
        customerFilterService.filterByTriggerConditions(candidateCustomerIds, testWorkflow);

    // Then
    assertEquals(churnRiskCustomers, result);
    verify(customerQueryService).filterHighChurnRiskCustomers(candidateCustomerIds);
  }

  @Test
  @DisplayName("트리거 조건 필터링 - 알 수 없는 트리거 타입")
  void filterByTriggerConditions_알수없는트리거타입() {
    // Given
    testWorkflow = Workflow.builder().triggerType("unknown-trigger").build();

    // When
    List<Long> result =
        customerFilterService.filterByTriggerConditions(candidateCustomerIds, testWorkflow);

    // Then
    assertEquals(candidateCustomerIds, result);
    verifyNoInteractions(customerQueryService);
  }

  @Test
  @DisplayName("필터링 중 예외 발생 - 빈 리스트 반환")
  void filterTargetCustomerIds_예외발생_빈리스트반환() {
    // Given
    when(customerQueryService.findCustomerIdsByShopId(100L))
        .thenThrow(new RuntimeException("Database error"));

    // When
    List<Long> result = customerFilterService.filterTargetCustomerIds(testWorkflow);

    // Then
    assertTrue(result.isEmpty());
  }
}
