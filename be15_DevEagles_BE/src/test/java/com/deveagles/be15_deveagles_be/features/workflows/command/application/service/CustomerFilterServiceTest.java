package com.deveagles.be15_deveagles_be.features.workflows.command.application.service;

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
@DisplayName("고객 필터링 서비스 테스트")
class CustomerFilterServiceTest {

  @Mock private ObjectMapper objectMapper;

  @Mock private CustomerQueryService customerQueryService;

  @Mock private MessageQueryService messageQueryService;

  @InjectMocks private CustomerFilterService customerFilterService;

  private Workflow testWorkflow;

  @BeforeEach
  void setUp() {
    testWorkflow =
        Workflow.builder()
            .id(1L)
            .shopId(100L)
            .targetCustomerGrades("[1, 2, 3]") // 등급 ID들
            .targetTags("[10, 20]") // 태그 ID들
            .excludeDormantCustomers(true)
            .dormantPeriodMonths(6)
            .excludeRecentMessageReceivers(true)
            .recentMessagePeriodDays(30)
            .build();
  }

  @Test
  @DisplayName("전체 필터링 프로세스 - 정상 케이스")
  void filterTargetCustomerIds_전체프로세스_성공() throws Exception {
    // Given
    List<Long> initialCustomers = Arrays.asList(1L, 2L, 3L, 4L, 5L);
    List<Long> gradeFilteredCustomers = Arrays.asList(1L, 2L, 3L, 4L);
    List<Long> tagFilteredCustomers = Arrays.asList(1L, 2L, 3L);
    List<Long> dormantFilteredCustomers = Arrays.asList(1L, 2L);
    List<Long> finalFilteredCustomers = Arrays.asList(1L, 2L);

    List<Long> gradeIds = Arrays.asList(1L, 2L, 3L);
    List<Long> tagIds = Arrays.asList(10L, 20L);

    when(customerQueryService.findCustomerIdsByShopId(100L)).thenReturn(initialCustomers);

    // JSON 파싱 모킹
    doReturn(gradeIds).when(objectMapper).readValue(eq("[1, 2, 3]"), any(TypeReference.class));
    doReturn(tagIds).when(objectMapper).readValue(eq("[10, 20]"), any(TypeReference.class));

    when(customerQueryService.filterCustomersByGradeIds(initialCustomers, gradeIds))
        .thenReturn(gradeFilteredCustomers);
    when(customerQueryService.filterCustomersByTagIds(gradeFilteredCustomers, tagIds))
        .thenReturn(tagFilteredCustomers);
    when(customerQueryService.excludeDormantCustomers(
            eq(tagFilteredCustomers), any(LocalDateTime.class), eq(100L)))
        .thenReturn(dormantFilteredCustomers);
    when(messageQueryService.excludeRecentMessageReceivers(
            eq(dormantFilteredCustomers), any(LocalDateTime.class), eq(100L)))
        .thenReturn(finalFilteredCustomers);

    // When
    List<Long> result = customerFilterService.filterTargetCustomerIds(testWorkflow);

    // Then
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(1L, 2L), result);

    // 모든 필터링 단계가 순서대로 호출되었는지 확인
    verify(customerQueryService).findCustomerIdsByShopId(100L);
    verify(customerQueryService).filterCustomersByGradeIds(initialCustomers, gradeIds);
    verify(customerQueryService).filterCustomersByTagIds(gradeFilteredCustomers, tagIds);
    verify(customerQueryService)
        .excludeDormantCustomers(eq(tagFilteredCustomers), any(LocalDateTime.class), eq(100L));
    verify(messageQueryService)
        .excludeRecentMessageReceivers(
            eq(dormantFilteredCustomers), any(LocalDateTime.class), eq(100L));
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

    // 고객이 없으면 추가 필터링은 수행되지 않음
    verify(customerQueryService, never()).filterCustomersByGradeIds(any(), any());
    verify(customerQueryService, never()).filterCustomersByTagIds(any(), any());
  }

  @Test
  @DisplayName("등급 필터링 없음 - null인 경우")
  void filterTargetCustomerIds_등급필터링없음_전체고객반환() throws Exception {
    // Given
    testWorkflow =
        Workflow.builder()
            .shopId(100L)
            .targetCustomerGrades(null) // 등급 조건 없음
            .targetTags(null) // 태그 조건 없음
            .excludeDormantCustomers(false)
            .excludeRecentMessageReceivers(false)
            .build();

    List<Long> allCustomers = Arrays.asList(1L, 2L, 3L, 4L, 5L);

    when(customerQueryService.findCustomerIdsByShopId(100L)).thenReturn(allCustomers);

    // When
    List<Long> result = customerFilterService.filterTargetCustomerIds(testWorkflow);

    // Then
    assertEquals(5, result.size());
    assertEquals(allCustomers, result);

    // 필터링이 적용되지 않음
    verify(customerQueryService, never()).filterCustomersByGradeIds(any(), any());
    verify(customerQueryService, never()).filterCustomersByTagIds(any(), any());
    verify(customerQueryService, never()).excludeDormantCustomers(any(), any(), any());
    verify(messageQueryService, never()).excludeRecentMessageReceivers(any(), any(), any());
  }

  @Test
  @DisplayName("JSON 파싱 오류 - 기본값으로 처리")
  void filterTargetCustomerIds_JSON파싱오류_기본값처리() throws Exception {
    // Given
    // 파싱 오류 테스트를 위한 단순한 워크플로우 (등급 필터링만)
    Workflow parsingErrorWorkflow =
        Workflow.builder()
            .id(1L)
            .shopId(100L)
            .targetCustomerGrades("[1, 2, 3]")
            .targetTags(null) // 태그 필터링 제외
            .excludeDormantCustomers(false)
            .excludeRecentMessageReceivers(false)
            .build();

    List<Long> initialCustomers = Arrays.asList(1L, 2L, 3L);

    when(customerQueryService.findCustomerIdsByShopId(100L)).thenReturn(initialCustomers);

    // JSON 파싱 오류 발생
    doThrow(new JsonProcessingException("JSON 파싱 오류") {})
        .when(objectMapper)
        .readValue(eq("[1, 2, 3]"), any(TypeReference.class));

    // When
    List<Long> result = customerFilterService.filterTargetCustomerIds(parsingErrorWorkflow);

    // Then
    // 파싱 오류 시 원본 고객 리스트가 반환됨 (필터링 스킵)
    assertEquals(3, result.size());
    assertEquals(initialCustomers, result);
  }

  @Test
  @DisplayName("휴면 고객 제외 설정 - 비활성화")
  void filterTargetCustomerIds_휴면고객제외비활성화() throws Exception {
    // Given
    testWorkflow =
        Workflow.builder()
            .shopId(100L)
            .targetCustomerGrades("[1]")
            .targetTags("[10]")
            .excludeDormantCustomers(false) // 휴면 고객 제외 안함
            .excludeRecentMessageReceivers(false)
            .build();

    List<Long> customers = Arrays.asList(1L, 2L);

    when(customerQueryService.findCustomerIdsByShopId(100L)).thenReturn(customers);
    doReturn(Arrays.asList(1L)).when(objectMapper).readValue(anyString(), any(TypeReference.class));
    when(customerQueryService.filterCustomersByGradeIds(any(), any())).thenReturn(customers);
    when(customerQueryService.filterCustomersByTagIds(any(), any())).thenReturn(customers);

    // When
    List<Long> result = customerFilterService.filterTargetCustomerIds(testWorkflow);

    // Then
    assertEquals(2, result.size());

    // 휴면 고객 제외가 호출되지 않음
    verify(customerQueryService, never()).excludeDormantCustomers(any(), any(), any());
    verify(messageQueryService, never()).excludeRecentMessageReceivers(any(), any(), any());
  }

  @Test
  @DisplayName("트리거별 특별 조건 필터링 - 생일 트리거")
  void filterByTriggerConditions_생일트리거() {
    // Given
    Workflow birthdayWorkflow = Workflow.builder().triggerType("birthday").build();

    List<Long> customerIds = Arrays.asList(1L, 2L, 3L);
    List<Long> birthdayCustomers = Arrays.asList(2L);

    when(customerQueryService.filterTodayBirthdayCustomers(customerIds))
        .thenReturn(birthdayCustomers);

    // When
    List<Long> result =
        customerFilterService.filterByTriggerConditions(customerIds, birthdayWorkflow);

    // Then
    assertEquals(1, result.size());
    assertEquals(Arrays.asList(2L), result);
    verify(customerQueryService).filterTodayBirthdayCustomers(customerIds);
  }

  @Test
  @DisplayName("트리거별 특별 조건 필터링 - 방문 주기 트리거")
  void filterByTriggerConditions_방문주기트리거() {
    // Given
    Workflow visitCycleWorkflow =
        Workflow.builder()
            .triggerType("visit-cycle")
            .triggerConfig("{\"visitCycleDays\":30}")
            .build();

    List<Long> customerIds = Arrays.asList(1L, 2L, 3L);
    List<Long> visitCycleCustomers = Arrays.asList(1L, 3L);

    when(customerQueryService.filterVisitCycleCustomers(customerIds, "{\"visitCycleDays\":30}"))
        .thenReturn(visitCycleCustomers);

    // When
    List<Long> result =
        customerFilterService.filterByTriggerConditions(customerIds, visitCycleWorkflow);

    // Then
    assertEquals(2, result.size());
    assertEquals(Arrays.asList(1L, 3L), result);
    verify(customerQueryService).filterVisitCycleCustomers(customerIds, "{\"visitCycleDays\":30}");
  }

  @Test
  @DisplayName("트리거별 특별 조건 필터링 - 알 수 없는 트리거")
  void filterByTriggerConditions_알수없는트리거() {
    // Given
    Workflow unknownWorkflow = Workflow.builder().triggerType("unknown-trigger").build();

    List<Long> customerIds = Arrays.asList(1L, 2L, 3L);

    // When
    List<Long> result =
        customerFilterService.filterByTriggerConditions(customerIds, unknownWorkflow);

    // Then
    // 알 수 없는 트리거는 원본 리스트 그대로 반환
    assertEquals(3, result.size());
    assertEquals(customerIds, result);

    // 어떤 특별 필터링도 호출되지 않음
    verify(customerQueryService, never()).filterTodayBirthdayCustomers(any());
    verify(customerQueryService, never()).filterVisitCycleCustomers(any(), any());
  }

  @Test
  @DisplayName("필터링 예외 처리 - 전체 프로세스 예외 시 빈 리스트 반환")
  void filterTargetCustomerIds_예외발생_빈리스트반환() {
    // Given
    when(customerQueryService.findCustomerIdsByShopId(100L))
        .thenThrow(new RuntimeException("데이터베이스 오류"));

    // When
    List<Long> result = customerFilterService.filterTargetCustomerIds(testWorkflow);

    // Then
    assertTrue(result.isEmpty());
  }
}
