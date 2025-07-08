package com.deveagles.be15_deveagles_be.features.workflows.execution.infrastructure.service;

import com.deveagles.be15_deveagles_be.features.customers.query.dto.response.CustomerDetailResponse;
import com.deveagles.be15_deveagles_be.features.customers.query.service.CustomerQueryService;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.repository.WorkflowRepository;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.vo.TriggerConfig;
import com.deveagles.be15_deveagles_be.features.workflows.execution.application.service.TriggerCheckService;
import com.deveagles.be15_deveagles_be.features.workflows.execution.application.service.WorkflowExecutionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TriggerCheckServiceImpl implements TriggerCheckService {

  private final WorkflowRepository workflowRepository;
  private final WorkflowExecutionService workflowExecutionService;
  private final ObjectMapper objectMapper;
  private final CustomerQueryService customerQueryService;

  @EventListener
  @Override
  public void onCustomerVisit(CustomerVisitEvent event) {
    log.debug("고객 방문 이벤트 처리: 고객 ID={}, 매장 ID={}", event.getCustomerId(), event.getShopId());

    try {
      checkVisitCycleTriggers(event);
      checkBirthdayTriggers(event);

    } catch (Exception e) {
      log.error("고객 방문 트리거 처리 중 오류: 고객 ID={}, 오류={}", event.getCustomerId(), e.getMessage(), e);
    }
  }

  @EventListener
  @Override
  public void onCustomerRegistration(CustomerRegistrationEvent event) {
    log.debug("고객 등록 이벤트 처리: 고객 ID={}, 매장 ID={}", event.getCustomerId(), event.getShopId());

    try {
      List<Workflow> newCustomerWorkflows =
          workflowRepository.findByTriggerTypeAndShopId("new-customer-followup", event.getShopId());

      for (Workflow workflow : newCustomerWorkflows) {
        if (workflow.canExecute()) {
          log.info(
              "신규 고객 팔로업 워크플로우 실행: 워크플로우 ID={}, 고객 ID={}", workflow.getId(), event.getCustomerId());
          workflowExecutionService.executeTriggeredWorkflow(workflow, event.getCustomerId());
        }
      }

    } catch (Exception e) {
      log.error("고객 등록 트리거 처리 중 오류: 고객 ID={}, 오류={}", event.getCustomerId(), e.getMessage(), e);
    }
  }

  @EventListener
  @Override
  public void onPaymentCompleted(PaymentCompletedEvent event) {
    log.debug(
        "결제 완료 이벤트 처리: 고객 ID={}, 매장 ID={}, 금액={}",
        event.getCustomerId(),
        event.getShopId(),
        event.getAmount());

    try {
      checkAmountMilestoneTriggers(event);

    } catch (Exception e) {
      log.error("결제 완료 트리거 처리 중 오류: 고객 ID={}, 오류={}", event.getCustomerId(), e.getMessage(), e);
    }
  }

  private void checkVisitCycleTriggers(CustomerVisitEvent event) {
    List<Workflow> visitCycleWorkflows =
        workflowRepository.findByTriggerTypeAndShopId("visit-cycle", event.getShopId());

    for (Workflow workflow : visitCycleWorkflows) {
      if (!workflow.canExecute()) continue;

      try {
        TriggerConfig triggerConfig = parseTriggerConfig(workflow.getTriggerConfig());

        // 고객 정보 조회하여 실제 방문 패턴 확인
        Optional<CustomerDetailResponse> customerOpt =
            customerQueryService.getCustomerDetail(event.getCustomerId(), event.getShopId());
        if (customerOpt.isEmpty()) {
          log.warn("고객 정보를 찾을 수 없습니다. customerId: {}", event.getCustomerId());
          continue;
        }

        CustomerDetailResponse customer = customerOpt.get();
        boolean shouldTrigger = checkVisitCycle(customer, triggerConfig.getVisitCycleDays());

        if (shouldTrigger) {
          log.info(
              "방문 주기 트리거 실행: 워크플로우 ID={}, 고객 ID={}, 방문 횟수={}회",
              workflow.getId(),
              event.getCustomerId(),
              customer.getVisitCount());
          workflowExecutionService.executeTriggeredWorkflow(workflow, event.getCustomerId());
        }

      } catch (Exception e) {
        log.error("방문 주기 트리거 체크 중 오류: 워크플로우 ID={}, 오류={}", workflow.getId(), e.getMessage());
      }
    }
  }

  private void checkBirthdayTriggers(CustomerVisitEvent event) {
    List<Workflow> birthdayWorkflows =
        workflowRepository.findByTriggerTypeAndShopId("birthday", event.getShopId());

    for (Workflow workflow : birthdayWorkflows) {
      if (!workflow.canExecute()) continue;

      try {
        TriggerConfig triggerConfig = parseTriggerConfig(workflow.getTriggerConfig());

        // 고객 정보 조회하여 생일 확인
        Optional<CustomerDetailResponse> customerOpt =
            customerQueryService.getCustomerDetail(event.getCustomerId(), event.getShopId());
        if (customerOpt.isEmpty() || customerOpt.get().getBirthdate() == null) {
          continue;
        }

        CustomerDetailResponse customer = customerOpt.get();
        boolean shouldTrigger =
            checkBirthdayApproaching(
                customer.getBirthdate(), triggerConfig.getBirthdayDaysBefore());

        if (shouldTrigger) {
          log.info(
              "생일 트리거 실행: 워크플로우 ID={}, 고객 ID={}, 생일={}",
              workflow.getId(),
              event.getCustomerId(),
              customer.getBirthdate());
          workflowExecutionService.executeTriggeredWorkflow(workflow, event.getCustomerId());
        }

      } catch (Exception e) {
        log.error("생일 트리거 체크 중 오류: 워크플로우 ID={}, 오류={}", workflow.getId(), e.getMessage());
      }
    }
  }

  private void checkAmountMilestoneTriggers(PaymentCompletedEvent event) {
    List<Workflow> amountWorkflows =
        workflowRepository.findByTriggerTypeAndShopId("amount-milestone", event.getShopId());

    for (Workflow workflow : amountWorkflows) {
      if (!workflow.canExecute()) continue;

      try {
        TriggerConfig triggerConfig = parseTriggerConfig(workflow.getTriggerConfig());

        // 고객 정보 조회하여 누적 매출 확인
        Optional<CustomerDetailResponse> customerOpt =
            customerQueryService.getCustomerDetail(event.getCustomerId(), event.getShopId());
        if (customerOpt.isEmpty()) {
          continue;
        }

        CustomerDetailResponse customer = customerOpt.get();
        boolean shouldTrigger =
            checkAmountMilestone(customer.getTotalRevenue(), triggerConfig.getAmountMilestone());

        if (shouldTrigger) {
          log.info(
              "누적 금액 마일스톤 트리거 실행: 워크플로우 ID={}, 고객 ID={}, 누적매출={}원",
              workflow.getId(),
              event.getCustomerId(),
              customer.getTotalRevenue());
          workflowExecutionService.executeTriggeredWorkflow(workflow, event.getCustomerId());
        }

      } catch (Exception e) {
        log.error("누적 금액 마일스톤 트리거 체크 중 오류: 워크플로우 ID={}, 오류={}", workflow.getId(), e.getMessage());
      }
    }
  }

  private boolean checkVisitCycle(CustomerDetailResponse customer, Integer cycleDays) {
    if (cycleDays == null || customer.getRecentVisitDate() == null) {
      return false;
    }

    // 최근 방문일로부터 설정된 주기가 지났는지 확인
    long daysSinceLastVisit =
        ChronoUnit.DAYS.between(customer.getRecentVisitDate(), LocalDate.now());

    // 설정된 주기와 일치하거나 조금 지났을 때 트리거 (±1일 허용)
    return Math.abs(daysSinceLastVisit - cycleDays) <= 1;
  }

  private boolean checkBirthdayApproaching(LocalDate birthdate, Integer daysBefore) {
    if (daysBefore == null || birthdate == null) {
      return false;
    }

    LocalDate today = LocalDate.now();
    LocalDate thisYearBirthday = birthdate.withYear(today.getYear());

    // 올해 생일이 이미 지났으면 내년 생일로 계산
    if (thisYearBirthday.isBefore(today)) {
      thisYearBirthday = thisYearBirthday.plusYears(1);
    }

    long daysUntilBirthday = ChronoUnit.DAYS.between(today, thisYearBirthday);
    return daysUntilBirthday == daysBefore;
  }

  private boolean checkAmountMilestone(Integer totalRevenue, Long amountMilestone) {
    if (amountMilestone == null || totalRevenue == null) {
      return false;
    }

    // 누적 매출이 마일스톤에 도달했는지 확인
    return totalRevenue >= amountMilestone;
  }

  private TriggerConfig parseTriggerConfig(String triggerConfigJson) {
    try {
      if (triggerConfigJson == null || triggerConfigJson.trim().isEmpty()) {
        return TriggerConfig.builder().build();
      }
      return objectMapper.readValue(triggerConfigJson, TriggerConfig.class);
    } catch (Exception e) {
      log.error("트리거 설정 파싱 오류: {}, JSON={}", e.getMessage(), triggerConfigJson);
      return TriggerConfig.builder().build();
    }
  }
}
