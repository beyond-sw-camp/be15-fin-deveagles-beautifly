package com.deveagles.be15_deveagles_be.features.campaigns.command.application.service;

import com.deveagles.be15_deveagles_be.features.campaigns.command.application.dto.request.CreateCampaignRequest;
import com.deveagles.be15_deveagles_be.features.campaigns.command.application.dto.response.CampaignResponse;

public interface CampaignCommandService {
  CampaignResponse createCampaign(CreateCampaignRequest request);

  void deleteCampaign(Long campaignId);
}
