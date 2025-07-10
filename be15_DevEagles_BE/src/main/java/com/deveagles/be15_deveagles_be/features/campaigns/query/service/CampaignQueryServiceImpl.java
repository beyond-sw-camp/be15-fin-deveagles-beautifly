package com.deveagles.be15_deveagles_be.features.campaigns.query.service;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.campaigns.command.domain.aggregate.Campaign;
import com.deveagles.be15_deveagles_be.features.campaigns.query.controller.CampaignQueryController;
import com.deveagles.be15_deveagles_be.features.campaigns.query.dto.request.CampaignSearchRequest;
import com.deveagles.be15_deveagles_be.features.campaigns.query.dto.response.CampaignQueryResponse;
import com.deveagles.be15_deveagles_be.features.campaigns.query.repository.CampaignQueryRepository;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.controller.CouponController;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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

    Page<CampaignQueryResponse> responsePage =
        campaignPage.map(
            campaign -> {
              CampaignQueryResponse response = CampaignQueryResponse.from(campaign);

              // HATEOAS 링크 추가
              response.add(
                  WebMvcLinkBuilder.linkTo(
                          WebMvcLinkBuilder.methodOn(CampaignQueryController.class)
                              .getCampaignsByShop(campaign.getShopId(), 0, 10))
                      .withSelfRel());

              // 쿠폰 정보가 있는 경우 쿠폰 링크 추가
              if (campaign.getCouponId() != null) {
                response.add(
                    WebMvcLinkBuilder.linkTo(
                            WebMvcLinkBuilder.methodOn(CouponController.class)
                                .getCouponById(null, (Long) campaign.getCouponId()))
                        .withRel("coupon"));
              }

              return response;
            });

    return PagedResult.from(responsePage);
  }

  @Override
  public Optional<CampaignQueryResponse> getCampaignById(Long id) {
    Optional<Campaign> campaign = campaignQueryRepository.findByIdAndDeletedAtIsNull(id);

    return campaign.map(
        c -> {
          CampaignQueryResponse response = CampaignQueryResponse.from(c);

          // HATEOAS 링크 추가
          response.add(
              WebMvcLinkBuilder.linkTo(
                      WebMvcLinkBuilder.methodOn(CampaignQueryController.class)
                          .getCampaignById(c.getId()))
                  .withSelfRel());

          // 쿠폰 정보가 있는 경우 쿠폰 링크 추가
          if (c.getCouponId() != null) {
            response.add(
                WebMvcLinkBuilder.linkTo(
                        WebMvcLinkBuilder.methodOn(CouponController.class)
                            .getCouponById(null, c.getCouponId()))
                    .withRel("coupon"));
          }

          // 캠페인 목록으로 돌아가는 링크
          response.add(
              WebMvcLinkBuilder.linkTo(
                      WebMvcLinkBuilder.methodOn(CampaignQueryController.class)
                          .getCampaignsByShop(c.getShopId(), 0, 10))
                  .withRel("campaigns"));

          return response;
        });
  }
}
