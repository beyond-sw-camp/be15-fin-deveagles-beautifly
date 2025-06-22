package com.deveagles.be15_deveagles_be.features.campaigns.command.application.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.campaigns.command.application.dto.request.CreateCampaignRequest;
import com.deveagles.be15_deveagles_be.features.campaigns.command.application.dto.response.CampaignResponse;
import com.deveagles.be15_deveagles_be.features.campaigns.command.domain.aggregate.Campaign;
import com.deveagles.be15_deveagles_be.features.campaigns.command.domain.repository.CampaignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CampaignCommandServiceImpl implements CampaignCommandService {

  private final CampaignRepository campaignRepository;

  @Override
  public CampaignResponse createCampaign(CreateCampaignRequest request) {
    validateCampaignDates(request);

    Campaign campaign =
        Campaign.builder()
            .campaignTitle(request.getCampaignTitle())
            .description(request.getDescription())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .messageSendAt(request.getMessageSendAt())
            .staffId(request.getStaffId())
            .templateId(request.getTemplateId())
            .couponId(request.getCouponId())
            .shopId(request.getShopId())
            .build();

    Campaign savedCampaign = campaignRepository.save(campaign);
    return CampaignResponse.from(savedCampaign);
  }

  @Override
  public void deleteCampaign(Long campaignId) {
    Campaign campaign =
        campaignRepository
            .findById(campaignId)
            .orElseThrow(() -> new BusinessException(ErrorCode.CAMPAIGN_NOT_FOUND));

    if (campaign.isDeleted()) {
      throw new BusinessException(ErrorCode.CAMPAIGN_ALREADY_DELETED);
    }

    campaign.softDelete();
    campaignRepository.save(campaign);
  }

  private void validateCampaignDates(CreateCampaignRequest request) {
    if (request.getEndDate().isBefore(request.getStartDate())) {
      throw new BusinessException(ErrorCode.INVALID_CAMPAIGN_DATE_RANGE);
    }
  }
}
