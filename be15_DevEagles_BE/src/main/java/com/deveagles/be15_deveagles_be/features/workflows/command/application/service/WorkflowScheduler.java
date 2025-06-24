package com.deveagles.be15_deveagles_be.features.workflows.command.application.service;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.repository.WorkflowRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WorkflowScheduler {

  private final WorkflowRepository workflowRepository;
  private final WorkflowExecutionService workflowExecutionService;

  /** 예약된 워크플로우들을 주기적으로 실행 매 1분마다 실행 */
  @Scheduled(fixedDelay = 60000)
  public void executeScheduledWorkflows() {
    log.debug("스케줄된 워크플로우 실행 체크 시작");

    try {
      LocalDateTime now = LocalDateTime.now();
      List<Workflow> scheduledWorkflows = workflowRepository.findScheduledWorkflows(now);

      if (scheduledWorkflows.isEmpty()) {
        log.debug("실행할 스케줄된 워크플로우가 없습니다.");
        return;
      }

      log.info("{}개의 스케줄된 워크플로우를 실행합니다.", scheduledWorkflows.size());

      for (Workflow workflow : scheduledWorkflows) {
        try {
          log.info("워크플로우 실행 시작: ID={}, 제목={}", workflow.getId(), workflow.getTitle());
          workflowExecutionService.executeWorkflow(workflow);
        } catch (Exception e) {
          log.error("워크플로우 실행 중 오류 발생: ID={}, 오류={}", workflow.getId(), e.getMessage(), e);
        }
      }

    } catch (Exception e) {
      log.error("스케줄된 워크플로우 체크 중 오류 발생: {}", e.getMessage(), e);
    }
  }

  /** 매일 오전 9시에 일일 트리거 체크 (생일, 기념일 등) */
  @Scheduled(cron = "0 0 9 * * *")
  public void checkDailyTriggers() {
    log.info("일일 트리거 체크 시작");

    try {
      // 생일 트리거 체크
      List<Workflow> birthdayWorkflows = workflowRepository.findByTriggerType("birthday");
      for (Workflow workflow : birthdayWorkflows) {
        if (workflow.canExecute()) {
          workflowExecutionService.executeWorkflow(workflow);
        }
      }

      // 기념일 트리거 체크
      List<Workflow> anniversaryWorkflows =
          workflowRepository.findByTriggerType("first-visit-anniversary");
      for (Workflow workflow : anniversaryWorkflows) {
        if (workflow.canExecute()) {
          workflowExecutionService.executeWorkflow(workflow);
        }
      }

      log.info("일일 트리거 체크 완료");

    } catch (Exception e) {
      log.error("일일 트리거 체크 중 오류 발생: {}", e.getMessage(), e);
    }
  }

  /** 매시간 정각에 주기적 트리거 체크 (방문 주기, 이탈 위험 등) */
  @Scheduled(cron = "0 0 * * * *")
  public void checkPeriodicTriggers() {
    log.debug("주기적 트리거 체크 시작");

    try {
      // 방문 주기 기반 트리거
      List<Workflow> visitCycleWorkflows = workflowRepository.findByTriggerType("visit-cycle");
      for (Workflow workflow : visitCycleWorkflows) {
        if (workflow.canExecute()) {
          workflowExecutionService.executeWorkflow(workflow);
        }
      }

      // 이탈 위험 고객 트리거
      List<Workflow> churnRiskWorkflows = workflowRepository.findByTriggerType("churn-risk-high");
      for (Workflow workflow : churnRiskWorkflows) {
        if (workflow.canExecute()) {
          workflowExecutionService.executeWorkflow(workflow);
        }
      }

      log.debug("주기적 트리거 체크 완료");

    } catch (Exception e) {
      log.error("주기적 트리거 체크 중 오류 발생: {}", e.getMessage(), e);
    }
  }
}
