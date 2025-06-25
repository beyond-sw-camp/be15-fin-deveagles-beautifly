package com.deveagles.be15_deveagles_be.features.workflows.command.application.service;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.repository.WorkflowRepository;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.vo.TriggerConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TriggerCheckService {

  private final WorkflowRepository workflowRepository;
  private final WorkflowExecutionService workflowExecutionService;
  private final ObjectMapper objectMapper;
  private final CustomerEventService customerEventService;

  /** 고객 방문 이벤트에 따른 트리거 체크 */
  @EventListener
  public void onCustomerVisit(CustomerVisitEvent event) {
    log.debug("고객 방문 이벤트 처리: 고객 ID={}, 매장 ID={}", event.getCustomerId(), event.getShopId());

    try {
      // 방문 주기 기반 워크플로우 체크
      checkVisitCycleTriggers(event);

      // 특정 시술 후 워크플로우 체크
      checkSpecificTreatmentTriggers(event);

      // 방문 횟수 마일스톤 체크
      checkVisitMilestoneTriggers(event);

    } catch (Exception e) {
      log.error("고객 방문 트리거 처리 중 오류: 고객 ID={}, 오류={}", event.getCustomerId(), e.getMessage(), e);
    }
  }

  /** 고객 등록 이벤트에 따른 트리거 체크 */
  @EventListener
  public void onCustomerRegistration(CustomerRegistrationEvent event) {
    log.debug("고객 등록 이벤트 처리: 고객 ID={}, 매장 ID={}", event.getCustomerId(), event.getShopId());

    try {
      // 신규 고객 팔로업 워크플로우 체크
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

  /** 결제 완료 이벤트에 따른 트리거 체크 */
  @EventListener
  public void onPaymentCompleted(PaymentCompletedEvent event) {
    log.debug(
        "결제 완료 이벤트 처리: 고객 ID={}, 매장 ID={}, 금액={}",
        event.getCustomerId(),
        event.getShopId(),
        event.getAmount());

    try {
      // 누적 금액 마일스톤 체크
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

        // 고객의 마지막 방문일 체크
        boolean shouldTrigger =
            customerEventService.checkVisitCycle(
                event.getCustomerId(), event.getShopId(), triggerConfig.getVisitCycleDays());

        if (shouldTrigger) {
          log.info("방문 주기 트리거 실행: 워크플로우 ID={}, 고객 ID={}", workflow.getId(), event.getCustomerId());
          workflowExecutionService.executeTriggeredWorkflow(workflow, event.getCustomerId());
        }

      } catch (Exception e) {
        log.error("방문 주기 트리거 체크 중 오류: 워크플로우 ID={}, 오류={}", workflow.getId(), e.getMessage());
      }
    }
  }

  private void checkSpecificTreatmentTriggers(CustomerVisitEvent event) {
    if (event.getTreatmentId() == null) return;

    List<Workflow> treatmentWorkflows =
        workflowRepository.findByTriggerTypeAndShopId("specific-treatment", event.getShopId());

    for (Workflow workflow : treatmentWorkflows) {
      if (!workflow.canExecute()) continue;

      try {
        TriggerConfig triggerConfig = parseTriggerConfig(workflow.getTriggerConfig());

        // 특정 시술과 매치되는지 체크
        if (event.getTreatmentId().equals(triggerConfig.getTreatmentId())) {
          log.info(
              "특정 시술 후 트리거 실행: 워크플로우 ID={}, 고객 ID={}, 시술 ID={}",
              workflow.getId(),
              event.getCustomerId(),
              event.getTreatmentId());
          workflowExecutionService.executeTriggeredWorkflow(workflow, event.getCustomerId());
        }

      } catch (Exception e) {
        log.error("특정 시술 트리거 체크 중 오류: 워크플로우 ID={}, 오류={}", workflow.getId(), e.getMessage());
      }
    }
  }

  private void checkVisitMilestoneTriggers(CustomerVisitEvent event) {
    List<Workflow> milestoneWorkflows =
        workflowRepository.findByTriggerTypeAndShopId("visit-milestone", event.getShopId());

    for (Workflow workflow : milestoneWorkflows) {
      if (!workflow.canExecute()) continue;

      try {
        TriggerConfig triggerConfig = parseTriggerConfig(workflow.getTriggerConfig());

        // 고객의 총 방문 횟수 체크
        int totalVisits =
            customerEventService.getTotalVisitCount(event.getCustomerId(), event.getShopId());

        if (totalVisits == triggerConfig.getVisitMilestone()) {
          log.info(
              "방문 횟수 마일스톤 트리거 실행: 워크플로우 ID={}, 고객 ID={}, 방문 횟수={}",
              workflow.getId(),
              event.getCustomerId(),
              totalVisits);
          workflowExecutionService.executeTriggeredWorkflow(workflow, event.getCustomerId());
        }

      } catch (Exception e) {
        log.error("방문 횟수 마일스톤 트리거 체크 중 오류: 워크플로우 ID={}, 오류={}", workflow.getId(), e.getMessage());
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

        // 고객의 누적 결제 금액 체크
        long totalAmount =
            customerEventService.getTotalPaymentAmount(event.getCustomerId(), event.getShopId());

        if (totalAmount >= triggerConfig.getAmountMilestone()) {
          log.info(
              "누적 금액 마일스톤 트리거 실행: 워크플로우 ID={}, 고객 ID={}, 누적 금액={}",
              workflow.getId(),
              event.getCustomerId(),
              totalAmount);
          workflowExecutionService.executeTriggeredWorkflow(workflow, event.getCustomerId());
        }

      } catch (Exception e) {
        log.error("누적 금액 마일스톤 트리거 체크 중 오류: 워크플로우 ID={}, 오류={}", workflow.getId(), e.getMessage());
      }
    }
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

/** 고객 관련 이벤트 클래스들 */
class CustomerVisitEvent {
  private final Long customerId;
  private final Long shopId;
  private final String treatmentId;
  private final java.time.LocalDateTime visitTime;

  public CustomerVisitEvent(
      Long customerId, Long shopId, String treatmentId, java.time.LocalDateTime visitTime) {
    this.customerId = customerId;
    this.shopId = shopId;
    this.treatmentId = treatmentId;
    this.visitTime = visitTime;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public Long getShopId() {
    return shopId;
  }

  public String getTreatmentId() {
    return treatmentId;
  }

  public java.time.LocalDateTime getVisitTime() {
    return visitTime;
  }
}

class CustomerRegistrationEvent {
  private final Long customerId;
  private final Long shopId;
  private final java.time.LocalDateTime registrationTime;

  public CustomerRegistrationEvent(
      Long customerId, Long shopId, java.time.LocalDateTime registrationTime) {
    this.customerId = customerId;
    this.shopId = shopId;
    this.registrationTime = registrationTime;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public Long getShopId() {
    return shopId;
  }

  public java.time.LocalDateTime getRegistrationTime() {
    return registrationTime;
  }
}

class PaymentCompletedEvent {
  private final Long customerId;
  private final Long shopId;
  private final Long amount;
  private final java.time.LocalDateTime paymentTime;

  public PaymentCompletedEvent(
      Long customerId, Long shopId, Long amount, java.time.LocalDateTime paymentTime) {
    this.customerId = customerId;
    this.shopId = shopId;
    this.amount = amount;
    this.paymentTime = paymentTime;
  }

  public Long getCustomerId() {
    return customerId;
  }

  public Long getShopId() {
    return shopId;
  }

  public Long getAmount() {
    return amount;
  }

  public java.time.LocalDateTime getPaymentTime() {
    return paymentTime;
  }
}

/** 고객 이벤트 서비스 인터페이스 */
interface CustomerEventService {
  boolean checkVisitCycle(Long customerId, Long shopId, Integer cycleDays);

  int getTotalVisitCount(Long customerId, Long shopId);

  long getTotalPaymentAmount(Long customerId, Long shopId);
}
