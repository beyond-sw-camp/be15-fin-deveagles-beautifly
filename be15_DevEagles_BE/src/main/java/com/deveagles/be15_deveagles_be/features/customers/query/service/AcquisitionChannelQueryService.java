package com.deveagles.be15_deveagles_be.features.customers.query.service;

import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.AcquisitionChannelResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AcquisitionChannelQueryService {

  AcquisitionChannelResponse getAcquisitionChannel(Long channelId);

  List<AcquisitionChannelResponse> getAllAcquisitionChannels();

  Page<AcquisitionChannelResponse> getAcquisitionChannels(Pageable pageable);
}
