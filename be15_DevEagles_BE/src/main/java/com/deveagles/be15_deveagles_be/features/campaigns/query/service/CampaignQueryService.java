package com.deveagles.be15_deveagles_be.features.campaigns.query.service;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.campaigns.query.dto.request.CampaignSearchRequest;
import com.deveagles.be15_deveagles_be.features.campaigns.query.dto.response.CampaignQueryResponse;
import java.util.Optional;

public interface CampaignQueryService {
  PagedResult<CampaignQueryResponse> getCampaignsByShop(CampaignSearchRequest request);

  Optional<CampaignQueryResponse> getCampaignById(Long id);
}
