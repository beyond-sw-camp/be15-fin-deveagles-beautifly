package com.deveagles.be15_deveagles_be.features.workflows.execution.infrastructure.service;

import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.Workflow;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.aggregate.WorkflowExecution;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.repository.WorkflowExecutionRepository;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.vo.ActionConfig;
import com.deveagles.be15_deveagles_be.features.workflows.command.domain.vo.ActionType;
import com.deveagles.be15_deveagles_be.features.workflows.execution.application.service.ActionExecutorService;
import com.deveagles.be15_deveagles_be.features.workflows.execution.application.service.WorkflowExecutionService.ActionExecutionResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActionExecutorServiceImpl implements ActionExecutorService {

  private final WorkflowExecutionRepository workflowExecutionRepository;
  private final ObjectMapper objectMapper;
  private final MessageServiceAdapter messageService;
  private final CouponServiceAdapter couponService;
  private final NotificationServiceAdapter notificationService;

  @Override
  public ActionExecutionResult executeAction(
      Workflow workflow, List<Long> targetCustomerIds, WorkflowExecution execution) {
    log.info(
        "액션 실행 시작: 워크플로우 ID={}, 대상 고객 {}명, 액션 타입={}",
        workflow.getId(),
        targetCustomerIds.size(),
        workflow.getActionType());

    try {
      ActionConfig actionConfig = parseActionConfig(workflow.getActionConfig());
      ActionType actionType = ActionType.fromCode(workflow.getActionType());

      if (!actionConfig.isValid(actionType)) {
        log.error(
            "액션 설정이 유효하지 않습니다: 워크플로우 ID={}, 액션 타입={}", workflow.getId(), workflow.getActionType());
        return new ActionExecutionResult(0, targetCustomerIds.size());
      }

      switch (actionType) {
        case MESSAGE_ONLY:
          return executeMessageOnlyAction(actionConfig, targetCustomerIds, workflow);
        case COUPON_MESSAGE:
          return executeCouponMessageAction(actionConfig, targetCustomerIds, workflow);
        case SYSTEM_NOTIFICATION:
          return executeSystemNotificationAction(actionConfig, targetCustomerIds, workflow);
        default:
          log.error("지원하지 않는 액션 타입: {}", workflow.getActionType());
          return new ActionExecutionResult(0, targetCustomerIds.size());
      }

    } catch (Exception e) {
      log.error("액션 실행 중 오류 발생: 워크플로우 ID={}, 오류={}", workflow.getId(), e.getMessage(), e);
      return new ActionExecutionResult(0, targetCustomerIds.size());
    }
  }

  private ActionExecutionResult executeMessageOnlyAction(
      ActionConfig actionConfig, List<Long> customerIds, Workflow workflow) {
    log.info("메시지 전용 액션 실행: 템플릿 ID={}", actionConfig.getMessageTemplateId());

    int successCount = 0;
    int failureCount = 0;

    for (Long customerId : customerIds) {
      try {
        boolean success = executeSmsAction(customerId, actionConfig, workflow.getShopId());
        if (success) {
          successCount++;
        } else {
          failureCount++;
        }
      } catch (Exception e) {
        log.error("메시지 발송 실패: 고객 ID={}, 오류={}", customerId, e.getMessage());
        failureCount++;
      }
    }

    log.info("메시지 전용 액션 완료: 성공={}, 실패={}", successCount, failureCount);
    return new ActionExecutionResult(successCount, failureCount);
  }

  private ActionExecutionResult executeCouponMessageAction(
      ActionConfig actionConfig, List<Long> customerIds, Workflow workflow) {
    log.info(
        "쿠폰+메시지 액션 실행: 쿠폰 ID={}, 템플릿 ID={}",
        actionConfig.getCouponId(),
        actionConfig.getMessageTemplateId());

    int successCount = 0;
    int failureCount = 0;

    if (!isValidCoupon(actionConfig.getCouponId(), workflow.getShopId())) {
      log.error("유효하지 않은 쿠폰: 쿠폰 ID={}, 매장 ID={}", actionConfig.getCouponId(), workflow.getShopId());
      return new ActionExecutionResult(0, customerIds.size());
    }

    for (Long customerId : customerIds) {
      try {
        boolean success = executeAlimtalkAction(customerId, actionConfig, workflow.getShopId());
        if (success) {
          successCount++;
        } else {
          failureCount++;
        }
      } catch (Exception e) {
        log.error("쿠폰+메시지 발송 실패: 고객 ID={}, 오류={}", customerId, e.getMessage());
        failureCount++;
      }
    }

    log.info("쿠폰+메시지 액션 완료: 성공={}, 실패={}", successCount, failureCount);
    return new ActionExecutionResult(successCount, failureCount);
  }

  private ActionExecutionResult executeSystemNotificationAction(
      ActionConfig actionConfig, List<Long> customerIds, Workflow workflow) {
    log.info("시스템 알림 액션 실행: 제목={}", actionConfig.getNotificationTitle());

    try {
      boolean success = executeNotificationAction(actionConfig, workflow.getShopId());
      if (success) {
        log.info("시스템 알림 발송 완료");
        return new ActionExecutionResult(1, 0);
      } else {
        log.error("시스템 알림 발송 실패");
        return new ActionExecutionResult(0, 1);
      }
    } catch (Exception e) {
      log.error("시스템 알림 발송 중 오류: {}", e.getMessage());
      return new ActionExecutionResult(0, 1);
    }
  }

  private boolean executeSmsAction(Long customerId, ActionConfig action, Long shopId) {
    String templateId = action.getMessageTemplateId();

    if (templateId == null) {
      log.warn("SMS 액션에 templateId가 없습니다. customerId: {}, shopId: {}", customerId, shopId);
      return false;
    }

    return messageService.sendMessage(customerId, shopId, templateId, action.getSendTime());
  }

  private boolean executeAlimtalkAction(Long customerId, ActionConfig action, Long shopId) {
    String templateId = action.getMessageTemplateId();
    String couponId = action.getCouponId();

    if (templateId == null) {
      log.warn("알림톡 액션에 templateId가 없습니다. customerId: {}, shopId: {}", customerId, shopId);
      return false;
    }

    return messageService.sendCouponMessage(
        customerId, shopId, templateId, couponId, action.getSendTime());
  }

  private boolean executeNotificationAction(ActionConfig action, Long shopId) {
    String title = action.getNotificationTitle();
    String content = action.getNotificationContent();
    String level = action.getNotificationLevel() != null ? action.getNotificationLevel() : "INFO";

    if (title == null || content == null) {
      log.warn("알림 액션에 필수 정보가 없습니다. title: {}, content: {}, shopId: {}", title, content, shopId);
      return false;
    }

    return notificationService.sendNotification(shopId, 1L, title, content, level, 1);
  }

  private boolean isValidCoupon(String couponId, Long shopId) {
    return couponService.isValidCoupon(couponId, shopId);
  }

  private ActionConfig parseActionConfig(String actionConfigJson) throws JsonProcessingException {
    if (actionConfigJson == null || actionConfigJson.trim().isEmpty()) {
      return ActionConfig.builder().build();
    }
    return objectMapper.readValue(actionConfigJson, ActionConfig.class);
  }
}
