package com.deveagles.be15_deveagles_be.features.workflows.execution.infrastructure.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// TODO: 나중에 지울 파일임

@Service
@Slf4j
@RequiredArgsConstructor
public class MockCustomerQueryService implements CustomerFilterServiceImpl.CustomerQueryService {

  private final ObjectMapper objectMapper;

  @Override
  public List<Long> findCustomerIdsByShopId(Long shopId) {
    log.info("Mock 매장별 고객 조회: 매장ID={}", shopId);
    // 임시로 고정된 고객 ID 목록 반환 (실제로는 DB에서 조회)
    return Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L);
  }

  @Override
  public List<Long> filterCustomersByGradeIds(List<Long> customerIds, List<Long> gradeIds) {
    log.info("Mock 고객 등급별 필터링: 고객수={}, 등급ID={}", customerIds.size(), gradeIds);
    // 임시로 절반 정도 필터링
    return customerIds.stream().filter(id -> id % 2 == 0).collect(Collectors.toList());
  }

  @Override
  public List<Long> filterCustomersByTagIds(List<Long> customerIds, List<Long> tagIds) {
    log.info("Mock 고객 태그별 필터링: 고객수={}, 태그ID={}", customerIds.size(), tagIds);
    // 임시로 3의 배수만 필터링
    return customerIds.stream().filter(id -> id % 3 == 0).collect(Collectors.toList());
  }

  @Override
  public List<Long> excludeDormantCustomers(
      List<Long> customerIds, LocalDateTime cutoffDate, Long shopId) {
    log.info("Mock 휴면 고객 제외: 고객수={}, 기준일={}, 매장ID={}", customerIds.size(), cutoffDate, shopId);
    // 임시로 5의 배수를 휴면 고객으로 간주하고 제외
    return customerIds.stream().filter(id -> id % 5 != 0).collect(Collectors.toList());
  }

  @Override
  public List<Long> filterTodayBirthdayCustomers(List<Long> customerIds) {
    log.info("Mock 오늘 생일인 고객 필터링: 고객수={}", customerIds.size());
    // 임시로 첫 번째 고객만 생일인 것으로 시뮬레이션
    return customerIds.isEmpty() ? customerIds : Arrays.asList(customerIds.get(0));
  }

  @Override
  public List<Long> filterVisitCycleCustomers(List<Long> customerIds, String triggerConfig) {
    log.info("Mock 방문 주기 고객 필터링: 고객수={}, 설정={}", customerIds.size(), triggerConfig);

    try {
      // triggerConfig에서 cycleDays 파싱
      Map<String, Object> config = objectMapper.readValue(triggerConfig, Map.class);
      Integer cycleDays = (Integer) config.get("cycleDays");

      log.info("방문 주기: {}일", cycleDays);

      // 임시로 홀수 ID 고객들을 방문 주기에 해당하는 것으로 시뮬레이션
      return customerIds.stream().filter(id -> id % 2 == 1).collect(Collectors.toList());

    } catch (JsonProcessingException e) {
      log.error("방문 주기 설정 파싱 오류: {}", e.getMessage());
      return customerIds;
    }
  }

  @Override
  public List<Long> filterAnniversaryCustomers(List<Long> customerIds) {
    log.info("Mock 첫 방문 기념일 고객 필터링: 고객수={}", customerIds.size());
    // 임시로 마지막 고객만 기념일인 것으로 시뮬레이션
    return customerIds.isEmpty()
        ? customerIds
        : Arrays.asList(customerIds.get(customerIds.size() - 1));
  }

  @Override
  public List<Long> filterHighChurnRiskCustomers(List<Long> customerIds) {
    log.info("Mock 이탈 위험 고객 필터링: 고객수={}", customerIds.size());
    // 임시로 4의 배수를 이탈 위험 고객으로 시뮬레이션
    return customerIds.stream().filter(id -> id % 4 == 0).collect(Collectors.toList());
  }
}
