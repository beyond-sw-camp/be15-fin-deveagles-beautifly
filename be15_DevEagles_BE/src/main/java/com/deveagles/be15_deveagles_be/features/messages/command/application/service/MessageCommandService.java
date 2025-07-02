package com.deveagles.be15_deveagles_be.features.messages.command.application.service;

import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.SmsRequest;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.response.SmsResponse;
import java.util.Collection;
import java.util.List;

public interface MessageCommandService {
  List<SmsResponse> sendSms(SmsRequest smsRequest);

  void markSmsAsFailed(Collection<Long> smsIds);
}
