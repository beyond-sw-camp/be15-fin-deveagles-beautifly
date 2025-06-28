package com.deveagles.be15_deveagles_be.features.items.command.application.service.impl;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.items.command.application.dto.request.SecondaryItemRegistRequest;
import com.deveagles.be15_deveagles_be.features.items.command.application.dto.request.SecondaryItemUpdateRequest;
import com.deveagles.be15_deveagles_be.features.items.command.application.service.SecondaryItemCommandService;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.PrimaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.SecondaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.repository.PrimaryItemRepository;
import com.deveagles.be15_deveagles_be.features.items.command.domain.repository.SecondaryItemRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecondaryItemCommandServiceImpl implements SecondaryItemCommandService {

  private final PrimaryItemRepository primaryItemRepository;
  private final SecondaryItemRepository secondaryItemRepository;

  @Override
  public void registerSecondaryItem(SecondaryItemRegistRequest request) {
    if (request == null) {
      throw new BusinessException(ErrorCode.INVALID_SECONDARY_ITEM_INPUT);
    }

    if (request.getSecondaryItemName() == null || request.getSecondaryItemName().isBlank()) {
      throw new BusinessException(ErrorCode.SECONDARY_ITEM_NAME_REQUIRED);
    }

    if (request.getSecondaryItemPrice() == null) {
      throw new BusinessException(ErrorCode.SECONDARY_ITEM_PRICE_REQUIRED);
    }

    PrimaryItem primaryItem =
        primaryItemRepository
            .findById(request.getPrimaryItemId())
            .orElseThrow(() -> new BusinessException(ErrorCode.PRIMARY_ITEM_NOT_FOUND));

    if (primaryItem.getCategory().isService() && request.getTimeTaken() == null) {
      throw new BusinessException(ErrorCode.SECONDARY_ITEM_SERVICE_TIME_REQUIRED);
    }

    SecondaryItem secondaryItem =
        SecondaryItem.builder()
            .primaryItemId(request.getPrimaryItemId())
            .secondaryItemName(request.getSecondaryItemName())
            .secondaryItemPrice(request.getSecondaryItemPrice())
            .timeTaken(request.getTimeTaken())
            .isActive(true) // 기본값 true로 명시
            .createdAt(LocalDateTime.now())
            .modifiedAt(LocalDateTime.now())
            .build();

    secondaryItemRepository.save(secondaryItem);
  }

  @Override
  public void updateSecondaryItem(Long secondaryItemId, SecondaryItemUpdateRequest request) {
    if (request == null) {
      throw new BusinessException(ErrorCode.INVALID_SECONDARY_ITEM_INPUT);
    }

    if (request.getSecondaryItemName() == null || request.getSecondaryItemName().isBlank()) {
      throw new BusinessException(ErrorCode.SECONDARY_ITEM_NAME_REQUIRED);
    }

    if (request.getSecondaryItemPrice() == null) {
      throw new BusinessException(ErrorCode.SECONDARY_ITEM_PRICE_REQUIRED);
    }

    // 1차 상품 유효성 검증
    PrimaryItem primaryItem =
        primaryItemRepository
            .findById(request.getPrimaryItemId())
            .orElseThrow(() -> new BusinessException(ErrorCode.PRIMARY_ITEM_NOT_FOUND));

    // PrimaryItem 조회
    SecondaryItem secondaryItem =
        secondaryItemRepository
            .findById(secondaryItemId)
            .orElseThrow(() -> new BusinessException(ErrorCode.SECONDARY_ITEM_NOT_FOUND));
    // 시술인 경우 시간 필수
    if (primaryItem.getCategory().isService() && request.getTimeTaken() == null) {
      throw new BusinessException(ErrorCode.SECONDARY_ITEM_SERVICE_TIME_REQUIRED);
    }

    // 필드 수정
    secondaryItem.updateSecondaryItem(
        request.getSecondaryItemName(),
        request.getSecondaryItemPrice(),
        request.getTimeTaken(),
        request.isActive());

    secondaryItem.setModifiedAt();

    // 저장
    secondaryItemRepository.save(secondaryItem);
  }

  @Override
  public void deleteSecondaryItem(Long id) {
    SecondaryItem item =
        secondaryItemRepository
            .findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.SECONDARY_ITEM_NOT_FOUND));

    item.setDeletedAt();
    secondaryItemRepository.save(item);
  }
}
