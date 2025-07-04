package com.deveagles.be15_deveagles_be.features.items.command.application.service.impl;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.items.command.application.dto.request.PrimaryItemRequest;
import com.deveagles.be15_deveagles_be.features.items.command.application.service.PrimaryItemCommandService;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.PrimaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.SecondaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.repository.PrimaryItemRepository;
import com.deveagles.be15_deveagles_be.features.items.command.domain.repository.SecondaryItemRepository;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.ShopRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrimaryItemCommandServiceImpl implements PrimaryItemCommandService {

  private final PrimaryItemRepository primaryItemRepository;
  private final SecondaryItemRepository secondaryItemRepository;
  private final ShopRepository shopRepository;

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

    if (Objects.isNull(request.getShopId())) {
      throw new BusinessException(ErrorCode.ITEMS_SHOP_ID_REQUIRED);
    }

    // Shop 조회
    Shop shop =
        shopRepository
            .findById(request.getShopId())
            .orElseThrow(() -> new BusinessException(ErrorCode.ITEMS_SHOP_NOT_FOUND));

    // PrimaryItem 생성
    PrimaryItem primaryItem =
        PrimaryItem.builder()
            .shopId(shop)
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

    if (Objects.isNull(request.getShopId())) {
      throw new BusinessException(ErrorCode.ITEMS_SHOP_ID_REQUIRED);
    }

    // Shop 유효성 검증
    boolean shopExists = shopRepository.existsById(request.getShopId());
    if (!shopExists) {
      throw new BusinessException(ErrorCode.ITEMS_SHOP_NOT_FOUND);
    }

    // PrimaryItem 조회
    PrimaryItem primaryItem =
        primaryItemRepository
            .findById(primaryItemId)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRIMARY_ITEM_NOT_FOUND));

    // 필드 수정
    primaryItem.updatePrimaryItem(request.getPrimaryItemName(), request.getCategory());

    primaryItemRepository.save(primaryItem);
  }

  @Override
  public void deletePrimaryItem(Long id) {
    // 1. PrimaryItem 조회
    PrimaryItem item =
        primaryItemRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.PRIMARY_ITEM_NOT_FOUND));

    // 2. 연관된 SecondaryItem 리스트 조회
    List<SecondaryItem> secondaryItems = secondaryItemRepository.findByPrimaryItemId(id);

    // 3. SecondaryItem soft delete
    for (SecondaryItem secondaryItem : secondaryItems) {
      secondaryItem.setDeletedAt();
      secondaryItemRepository.save(secondaryItem);
    }

    // 4. PrimaryItem soft delete
    item.setDeletedAt();
    primaryItemRepository.save(item);
  }
}
