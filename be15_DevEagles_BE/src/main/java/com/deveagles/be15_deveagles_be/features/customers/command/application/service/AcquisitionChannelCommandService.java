package com.deveagles.be15_deveagles_be.features.customers.command.application.service;

import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.CreateAcquisitionChannelRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.UpdateAcquisitionChannelRequest;

public interface AcquisitionChannelCommandService {

  Long createAcquisitionChannel(CreateAcquisitionChannelRequest request);

  void updateAcquisitionChannel(Long channelId, UpdateAcquisitionChannelRequest request);

  void deleteAcquisitionChannel(Long channelId);
}
