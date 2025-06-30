package com.deveagles.be15_deveagles_be.features.messages.command.application.service;

import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.request.MessageSettingRequest;
import com.deveagles.be15_deveagles_be.features.messages.command.application.dto.response.MessageSettingResponse;

public interface MessageSettingsService {
  Long createDefault(Long shopId);

  MessageSettingResponse loadSettings(Long shopId);

  void updateSettings(Long shopId, MessageSettingRequest request);
}
