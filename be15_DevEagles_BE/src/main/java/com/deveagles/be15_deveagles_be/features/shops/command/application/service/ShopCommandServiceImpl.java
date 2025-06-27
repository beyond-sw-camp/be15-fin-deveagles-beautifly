package com.deveagles.be15_deveagles_be.features.shops.command.application.service;

import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.ShopCreateRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.ValidBizNumberRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.response.GetIndustryResponse;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.ShopRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ShopCommandServiceImpl implements ShopCommandService {

  private final ShopRepository shopRepository;
  private final IndustryRepository industryRepository;

  @Override
  public Shop shopRegist(ShopCreateRequest request) {
    Shop shop =
        Shop.builder()
            .shopName(request.shopName())
            .address(request.address())
            .detailAddress(request.detailAddress())
            .industryId(request.industryId())
            .businessNumber(request.businessNumber())
            .phoneNumber(request.phoneNumber())
            .shopDescription(request.description())
            .build();

    return shopRepository.save(shop);
  }

  @Override
  public Boolean validCheckBizNumber(ValidBizNumberRequest request) {

    Optional<Shop> shop = shopRepository.findByBusinessNumber(request.bizNumber());

    return shop.isEmpty();
  }

  @Override
  public void patchOwnerId(Shop shop, Long ownerId) {

    shop.setOwner(ownerId);

    shopRepository.save(shop);
  }

  @Override
  public GetIndustryResponse getIndustry() {

    return GetIndustryResponse.builder().industryList(industryRepository.findAll()).build();
  }
}
