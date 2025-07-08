package com.deveagles.be15_deveagles_be.features.workflows.execution.infrastructure.service;

import com.deveagles.be15_deveagles_be.features.customers.query.dto.response.CustomerDetailResponse;
import com.deveagles.be15_deveagles_be.features.customers.query.service.CustomerQueryService;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.SmsRequest;
import com.deveagles.be15_deveagles_be.features.messages.command.application.service.MessageCommandService;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageKind;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageSendingType;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageType;
import com.deveagles.be15_deveagles_be.features.messages.query.service.MessageTemplateQueryService;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceAdapter {

  private final MessageCommandService messageCommandService;
  private final MessageTemplateQueryService messageTemplateQueryService;
  private final CustomerQueryService customerQueryService;

  public boolean sendMessage(Long customerId, Long shopId, String templateId, LocalTime sendTime) {
    try {
      // 고객 정보 조회
      Optional<CustomerDetailResponse> customerOpt =
          customerQueryService.getCustomerDetail(customerId, shopId);
      if (customerOpt.isEmpty()) {
        log.warn("고객을 찾을 수 없습니다. customerId: {}", customerId);
        return false;
      }
      CustomerDetailResponse customer = customerOpt.get();

      // 템플릿 조회
      Long tId = Long.parseLong(templateId);
      var template = messageTemplateQueryService.findOne(shopId, tId);
      if (template == null) {
        log.warn("메시지 템플릿을 찾을 수 없습니다. templateId: {}, shopId: {}", templateId, shopId);
        return false;
      }

      // SMS 요청 생성
      SmsRequest smsRequest =
          new SmsRequest(
              List.of(customerId),
              template.getTemplateContent(),
              MessageType.SMS,
              MessageSendingType.IMMEDIATE,
              LocalDateTime.now(),
              tId,
              false,
              null,
              null,
              MessageKind.advertising,
              null,
              null);

      // 메시지 발송
      var results = messageCommandService.sendSms(shopId, smsRequest);
      return !results.isEmpty() && results.get(0).success();

    } catch (Exception e) {
      log.error(
          "메시지 발송 실패 - customerId: {}, templateId: {}, shopId: {}",
          customerId,
          templateId,
          shopId,
          e);
      return false;
    }
  }

  public boolean sendCouponMessage(
      Long customerId, Long shopId, String templateId, String couponCode, LocalTime sendTime) {
    try {
      // 고객 정보 조회
      Optional<CustomerDetailResponse> customerOpt =
          customerQueryService.getCustomerDetail(customerId, shopId);
      if (customerOpt.isEmpty()) {
        log.warn("고객을 찾을 수 없습니다. customerId: {}", customerId);
        return false;
      }
      CustomerDetailResponse customer = customerOpt.get();

      // 템플릿 조회
      Long tId = Long.parseLong(templateId);
      var template = messageTemplateQueryService.findOne(shopId, tId);
      if (template == null) {
        log.warn("메시지 템플릿을 찾을 수 없습니다. templateId: {}, shopId: {}", templateId, shopId);
        return false;
      }

      // 템플릿 내용에 쿠폰 정보 추가
      String content = template.getTemplateContent();
      if (couponCode != null) {
        content = content.replace("{{couponCode}}", couponCode);
      }

      // SMS 요청 생성
      SmsRequest smsRequest =
          new SmsRequest(
              List.of(customerId),
              content,
              MessageType.SMS,
              MessageSendingType.IMMEDIATE,
              LocalDateTime.now(),
              tId,
              false,
              null,
              null,
              MessageKind.advertising,
              null,
              null);

      // 메시지 발송
      var results = messageCommandService.sendSms(shopId, smsRequest);
      return !results.isEmpty() && results.get(0).success();

    } catch (Exception e) {
      log.error(
          "쿠폰 메시지 발송 실패 - customerId: {}, templateId: {}, couponCode: {}, shopId: {}",
          customerId,
          templateId,
          couponCode,
          shopId,
          e);
      return false;
    }
  }
}
