package com.deveagles.be15_deveagles_be.features.shops.command.application.service;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.service.ReservationSettingInitializer;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.ShopCreateRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.ValidBizNumberRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.response.GetIndustryResponse;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.response.GetShopResponse;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Industry;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.SNS;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.ShopRepository;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.SnsRepository;
import java.util.List;
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
  private final SnsRepository snsRepository;
  private final ReservationSettingInitializer reservationSettingInitializer;

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

    Shop savedShop = shopRepository.save(shop);

    reservationSettingInitializer.initDefault(savedShop.getShopId());

    return savedShop;
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

  @Override
  public void validateShopExists(Long shopId) {
    if (!shopRepository.existsById(shopId)) {
      throw new BusinessException(ErrorCode.SHOP_NOT_FOUNT);
    }
  }

  @Override
  public GetShopResponse getShop(Long shopId) {

    Shop shop =
        shopRepository
            .findByShopId(shopId)
            .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_NOT_FOUNT));

    List<Industry> industryList = industryRepository.findAll();

    List<SNS> snsList = snsRepository.findByShopId(shopId);

    return GetShopResponse.builder()
        .shopName(shop.getShopName())
        .address(shop.getAddress())
        .detailAddress(shop.getDetailAddress())
        .phoneNumber(shop.getPhoneNumber())
        .bizNumber(shop.getBusinessNumber())
        .description(shop.getShopDescription())
        .industryId(shop.getIndustryId())
        .industryList(industryList)
        .snsList(snsList)
        .build();
  }
}
