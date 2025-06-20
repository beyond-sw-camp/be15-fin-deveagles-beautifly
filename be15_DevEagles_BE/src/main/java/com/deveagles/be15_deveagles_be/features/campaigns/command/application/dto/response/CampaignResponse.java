package com.deveagles.be15_deveagles_be.features.campaigns.command.application.dto.response;

import com.deveagles.be15_deveagles_be.features.campaigns.command.domain.aggregate.Campaign;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignResponse {

  private Long id;
  private String campaignTitle;
  private String description;

  private LocalDate startDate;
  private LocalDate endDate;
  private LocalDateTime messageSendAt;
  private LocalDateTime createdAt;

  private Long staffId;
  private Long templateId;
  private Long couponId;
  private Long shopId;

  public static CampaignResponse from(Campaign campaign) {
    return CampaignResponse.builder()
        .id(campaign.getId())
        .campaignTitle(campaign.getCampaignTitle())
        .description(campaign.getDescription())
        .startDate(campaign.getStartDate())
        .endDate(campaign.getEndDate())
        .messageSendAt(campaign.getMessageSendAt())
        .createdAt(campaign.getCreatedAt())
        .staffId(campaign.getStaffId())
        .templateId(campaign.getTemplateId())
        .couponId(campaign.getCouponId())
        .shopId(campaign.getShopId())
        .build();
  }
}
