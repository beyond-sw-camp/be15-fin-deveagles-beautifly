package com.deveagles.be15_deveagles_be.features.items.command.application.service.impl;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.items.command.application.dto.request.PrimaryItemRegistRequest;
import com.deveagles.be15_deveagles_be.features.items.command.application.service.PrimaryItemServiceCommand;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.PrimaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.repository.PrimaryItemRepository;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrimaryItemCommandServiceImpl implements PrimaryItemServiceCommand {

  private final PrimaryItemRepository primaryItemRepository;

  @Override
  public void registerPrimaryItem(PrimaryItemRegistRequest request) {
    // 유효성 검사
    if (request == null) {
      throw new BusinessException(ErrorCode.INVALID_PRIMARY_ITEM_INPUT);
    }

    if (Objects.isNull(request.getPrimaryItemName()) || request.getPrimaryItemName().isBlank()) {
      throw new BusinessException(ErrorCode.PRIMARY_ITEM_NAME_REQUIRED);
    }

    if (Objects.isNull(request.getCategory())) {
      throw new BusinessException(ErrorCode.PRIMARY_ITEM_CATEGORY_REQUIRED);
    }

    // 하드코딩된 Shop 객체 (shop_id = 1)
    Shop dummyShop = Shop.builder().shopId(1L).build();

    PrimaryItem primaryItem =
        PrimaryItem.builder()
            .shopId(dummyShop)
            .primaryItemName(request.getPrimaryItemName())
            .category(request.getCategory())
            .createdAt(LocalDateTime.now())
            .build();

    primaryItemRepository.save(primaryItem);
  }
}
