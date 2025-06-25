package com.deveagles.be15_deveagles_be.features.items.command.application.service.impl;

import static com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.QPrimaryItem.primaryItem;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.items.command.application.dto.request.PrimaryItemRequest;
import com.deveagles.be15_deveagles_be.features.items.command.application.service.PrimaryItemCommandService;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.PrimaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.repository.PrimaryItemRepository;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrimaryItemCommandServiceImpl implements PrimaryItemCommandService {

  private final PrimaryItemRepository primaryItemRepository;

  @Override
  public void registerPrimaryItem(PrimaryItemRequest request) {
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

  @Override
  public void updatePrimaryItem(Long primaryItemId, PrimaryItemRequest request) {
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

    if (!Objects.equals(request.getShopId(), 1L)) {
      throw new BusinessException(ErrorCode.INVALID_SHOP_ID);
    }

    // 기존 엔티티 조회
    PrimaryItem primaryItem =
        primaryItemRepository
            .findById(primaryItemId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRIMARY_ITEM_NOT_FOUND));

    // 필드 수정
    primaryItem.updatePrimaryItem(request.getPrimaryItemName(), request.getCategory());

    // 저장
    primaryItemRepository.save(primaryItem);
  }
}
