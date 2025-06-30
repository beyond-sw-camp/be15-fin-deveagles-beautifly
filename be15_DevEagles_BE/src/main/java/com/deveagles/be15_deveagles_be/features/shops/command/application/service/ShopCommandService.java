package com.deveagles.be15_deveagles_be.features.shops.command.application.service;

import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.ShopCreateRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.ValidBizNumberRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.response.GetIndustryResponse;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;

public interface ShopCommandService {
  Shop shopRegist(ShopCreateRequest shopRequest);

  Boolean validCheckBizNumber(ValidBizNumberRequest validRequest);

  void patchOwnerId(Shop shop, Long staffId);

  GetIndustryResponse getIndustry();
}
