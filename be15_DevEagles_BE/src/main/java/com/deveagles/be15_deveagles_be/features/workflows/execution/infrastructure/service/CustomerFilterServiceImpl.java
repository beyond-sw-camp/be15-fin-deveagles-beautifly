package com.deveagles.be15_deveagles_be.features.workflows.execution.infrastructure.service;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import com.deveagles.be15_deveagles_be.features.workflows.execution.application.service.CustomerFilterService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerFilterServiceImpl implements CustomerFilterService {

  private final ObjectMapper objectMapper;
  private final CustomerQueryService customerQueryService;
  private final MessageQueryService messageQueryService;

  @Override
  public List<Long> filterTargetCustomerIds(Workflow workflow) {
    log.info("대상 고객 필터링 시작: 워크플로우 ID={}", workflow.getId());

    try {
      List<Long> candidateCustomerIds =
          customerQueryService.findCustomerIdsByShopId(workflow.getShopId());

      if (candidateCustomerIds.isEmpty()) {
        log.info("매장에 고객이 없습니다: 매장 ID={}", workflow.getShopId());
        return new ArrayList<>();
      }

      log.info("후보 고객 {}명 조회됨", candidateCustomerIds.size());

      candidateCustomerIds =
          filterByCustomerGrades(candidateCustomerIds, workflow.getTargetCustomerGrades());

      candidateCustomerIds = filterByCustomerTags(candidateCustomerIds, workflow.getTargetTags());

      if (workflow.getExcludeDormantCustomers()) {
        candidateCustomerIds =
            excludeDormantCustomers(
                candidateCustomerIds, workflow.getDormantPeriodMonths(), workflow.getShopId());
      }

      if (workflow.getExcludeRecentMessageReceivers()) {
        candidateCustomerIds =
            excludeRecentMessageReceivers(
                candidateCustomerIds, workflow.getRecentMessagePeriodDays(), workflow.getShopId());
      }

      log.info("최종 대상 고객 {}명 선별됨", candidateCustomerIds.size());
      return candidateCustomerIds;

    } catch (Exception e) {
      log.error("고객 필터링 중 오류 발생: 워크플로우 ID={}, 오류={}", workflow.getId(), e.getMessage(), e);
      return new ArrayList<>();
    }
  }

  @Override
  public List<Long> filterByTriggerConditions(List<Long> customerIds, Workflow workflow) {
    switch (workflow.getTriggerType()) {
      case "birthday":
        return customerQueryService.filterTodayBirthdayCustomers(customerIds);
      case "visit-cycle":
        return customerQueryService.filterVisitCycleCustomers(
            customerIds, workflow.getTriggerConfig());
      case "first-visit-anniversary":
        return customerQueryService.filterAnniversaryCustomers(customerIds);
      case "churn-risk-high":
        return customerQueryService.filterHighChurnRiskCustomers(customerIds);
      default:
        return customerIds;
    }
  }

  private List<Long> filterByCustomerGrades(List<Long> customerIds, String targetGradesJson) {
    if (targetGradesJson == null || targetGradesJson.trim().isEmpty()) {
      return customerIds;
    }

    try {
      List<Long> targetGradeIds =
          objectMapper.readValue(targetGradesJson, new TypeReference<List<Long>>() {});

      if (targetGradeIds.isEmpty()) {
        return customerIds;
      }

      log.debug("고객 등급 필터링: 대상 등급 ID={}", targetGradeIds);
      return customerQueryService.filterCustomersByGradeIds(customerIds, targetGradeIds);

    } catch (JsonProcessingException e) {
      log.error("고객 등급 JSON 파싱 오류: {}, JSON={}", e.getMessage(), targetGradesJson);
      return customerIds;
    }
  }

  private List<Long> filterByCustomerTags(List<Long> customerIds, String targetTagsJson) {
    if (targetTagsJson == null || targetTagsJson.trim().isEmpty()) {
      return customerIds;
    }

    try {
      List<Long> targetTagIds =
          objectMapper.readValue(targetTagsJson, new TypeReference<List<Long>>() {});

      if (targetTagIds.isEmpty()) {
        return customerIds;
      }

      log.debug("고객 태그 필터링: 대상 태그 ID={}", targetTagIds);
      return customerQueryService.filterCustomersByTagIds(customerIds, targetTagIds);

    } catch (JsonProcessingException e) {
      log.error("고객 태그 JSON 파싱 오류: {}, JSON={}", e.getMessage(), targetTagsJson);
      return customerIds;
    }
  }

  private List<Long> excludeDormantCustomers(
      List<Long> customerIds, Integer dormantPeriodMonths, Long shopId) {
    if (dormantPeriodMonths == null || dormantPeriodMonths <= 0) {
      return customerIds;
    }

    LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(dormantPeriodMonths);
    log.debug("휴면 고객 제외: 기준일={}", cutoffDate);

    return customerQueryService.excludeDormantCustomers(customerIds, cutoffDate, shopId);
  }

  private List<Long> excludeRecentMessageReceivers(
      List<Long> customerIds, Integer recentPeriodDays, Long shopId) {
    if (recentPeriodDays == null || recentPeriodDays <= 0) {
      return customerIds;
    }

    LocalDateTime recentDate = LocalDateTime.now().minusDays(recentPeriodDays);
    log.debug("최근 메시지 수신자 제외: 기준일={}", recentDate);

    return messageQueryService.excludeRecentMessageReceivers(customerIds, recentDate, shopId);
  }

  interface CustomerQueryService {
    List<Long> findCustomerIdsByShopId(Long shopId);

    List<Long> filterCustomersByGradeIds(List<Long> customerIds, List<Long> gradeIds);

    List<Long> filterCustomersByTagIds(List<Long> customerIds, List<Long> tagIds);

    List<Long> excludeDormantCustomers(
        List<Long> customerIds, LocalDateTime cutoffDate, Long shopId);

    List<Long> filterTodayBirthdayCustomers(List<Long> customerIds);

    List<Long> filterVisitCycleCustomers(List<Long> customerIds, String triggerConfig);

    List<Long> filterAnniversaryCustomers(List<Long> customerIds);

    List<Long> filterHighChurnRiskCustomers(List<Long> customerIds);
  }

  interface MessageQueryService {
    List<Long> excludeRecentMessageReceivers(
        List<Long> customerIds, LocalDateTime recentDate, Long shopId);
  }
}
