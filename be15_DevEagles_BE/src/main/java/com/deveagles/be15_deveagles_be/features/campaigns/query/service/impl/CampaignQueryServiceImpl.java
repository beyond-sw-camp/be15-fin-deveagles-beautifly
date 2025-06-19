package com.deveagles.be15_deveagles_be.features.campaigns.query.service.impl;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.campaigns.command.domain.aggregate.Campaign;
import com.deveagles.be15_deveagles_be.features.campaigns.query.dto.request.CampaignSearchRequest;
import com.deveagles.be15_deveagles_be.features.campaigns.query.dto.response.CampaignQueryResponse;
import com.deveagles.be15_deveagles_be.features.campaigns.query.repository.CampaignQueryRepository;
import com.deveagles.be15_deveagles_be.features.campaigns.query.service.CampaignQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CampaignQueryServiceImpl implements CampaignQueryService {

  private final CampaignQueryRepository campaignQueryRepository;

  @Override
  public PagedResult<CampaignQueryResponse> getCampaignsByShop(CampaignSearchRequest request) {
    Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

    Page<Campaign> campaignPage =
        campaignQueryRepository.findByShopIdAndDeletedAtIsNullOrderByCreatedAtDesc(
            request.getShopId(), pageable);

    Page<CampaignQueryResponse> responsePage = campaignPage.map(CampaignQueryResponse::from);

    return PagedResult.from(responsePage);
  }
}
