package com.deveagles.be15_deveagles_be.features.messages.command.application.service;

import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.SmsRequest;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.response.SmsResponse;

public interface MessageCommandService {
  SmsResponse sendSms(SmsRequest smsRequest);

  void markSmsAsFailed(Long smsId);
}
