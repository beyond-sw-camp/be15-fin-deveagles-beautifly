package com.deveagles.be15_deveagles_be.features.shops.command.application.service;

import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.PutShopRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.ShopCreateRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.ValidBizNumberRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.response.GetIndustryResponse;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.response.GetShopResponse;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import jakarta.validation.Valid;

public interface ShopCommandService {
  Shop shopRegist(ShopCreateRequest shopRequest);

  Boolean validCheckBizNumber(ValidBizNumberRequest validRequest);

  void patchOwnerId(Shop shop, Long staffId);

  GetIndustryResponse getIndustry();

  void validateShopExists(Long shopId);

  GetShopResponse getShop(Long shopId);

  void putShop(Long shopId, @Valid PutShopRequest shopRequest);

  void updateReservationTerm(Long shopId, Integer term);
}
