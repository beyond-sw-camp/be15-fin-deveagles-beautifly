package com.deveagles.be15_deveagles_be.features.items.command.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.items.command.application.dto.request.PrimaryItemRequest;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.Category;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.PrimaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.repository.PrimaryItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PrimaryItemCommandServiceImplTest {

  private PrimaryItemRepository primaryItemRepository;
  private PrimaryItemCommandServiceImpl primaryItemCommandService;

  @BeforeEach
  void setUp() {
    primaryItemRepository = mock(PrimaryItemRepository.class);
    primaryItemCommandService = new PrimaryItemCommandServiceImpl(primaryItemRepository);
  }

  @Test
  @DisplayName("성공: 유효한 1차 상품 요청 등록")
  void registerPrimaryItem_success() {
    // given
    PrimaryItemRequest request = new PrimaryItemRequest();
    request.setShopId(1L);
    request.setCategory(Category.SERVICE);
    request.setPrimaryItemName("컷트");

    // when
    primaryItemCommandService.registerPrimaryItem(request);

    // then
    verify(primaryItemRepository, times(1)).save(any(PrimaryItem.class));
  }

  @Test
  @DisplayName("실패: request가 null일 경우 예외 발생")
  void registerPrimaryItem_nullRequest_throwsException() {
    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> primaryItemCommandService.registerPrimaryItem(null));

    assertEquals(ErrorCode.INVALID_PRIMARY_ITEM_INPUT, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패: 1차 상품명이 null 또는 공백일 경우 예외 발생")
  void registerPrimaryItem_missingName_throwsException() {
    PrimaryItemRequest request = new PrimaryItemRequest();
    request.setShopId(1L);
    request.setCategory(Category.SERVICE);
    request.setPrimaryItemName(" ");

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> primaryItemCommandService.registerPrimaryItem(request));

    assertEquals(ErrorCode.PRIMARY_ITEM_NAME_REQUIRED, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패: 카테고리가 null일 경우 예외 발생")
  void registerPrimaryItem_missingCategory_throwsException() {
    PrimaryItemRequest request = new PrimaryItemRequest();
    request.setShopId(1L);
    request.setPrimaryItemName("컷트");
    request.setCategory(null);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> primaryItemCommandService.registerPrimaryItem(request));

    assertEquals(ErrorCode.PRIMARY_ITEM_CATEGORY_REQUIRED, exception.getErrorCode());
  }
}
