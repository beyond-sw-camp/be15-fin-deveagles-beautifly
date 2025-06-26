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

  @Test
  @DisplayName("성공: 유효한 1차 상품 수정 요청")
  void updatePrimaryItem_success() {
    // given
    Long primaryItemId = 1L;
    PrimaryItemRequest request = new PrimaryItemRequest();
    request.setPrimaryItemName("펌");
    request.setCategory(Category.SERVICE);

    PrimaryItem existingItem =
        PrimaryItem.builder()
            .primaryItemId(primaryItemId)
            .primaryItemName("컷트")
            .category(Category.PRODUCT)
            .build();

    when(primaryItemRepository.findById(primaryItemId))
        .thenReturn(java.util.Optional.of(existingItem));

    // when
    primaryItemCommandService.updatePrimaryItem(primaryItemId, request);

    // then
    assertEquals("펌", existingItem.getPrimaryItemName());
    assertEquals(Category.SERVICE, existingItem.getCategory());
    verify(primaryItemRepository, times(1)).save(existingItem);
  }

  @Test
  @DisplayName("실패: 수정 요청이 null일 경우 예외 발생")
  void updatePrimaryItem_nullRequest_throwsException() {
    Long primaryItemId = 1L;

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> primaryItemCommandService.updatePrimaryItem(primaryItemId, null));

    assertEquals(ErrorCode.INVALID_PRIMARY_ITEM_INPUT, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패: 수정 요청에서 이름이 null 또는 공백일 경우 예외 발생")
  void updatePrimaryItem_blankName_throwsException() {
    Long primaryItemId = 1L;
    PrimaryItemRequest request = new PrimaryItemRequest();
    request.setPrimaryItemName(" ");
    request.setCategory(Category.SERVICE);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> primaryItemCommandService.updatePrimaryItem(primaryItemId, request));

    assertEquals(ErrorCode.PRIMARY_ITEM_NAME_REQUIRED, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패: 수정 요청에서 카테고리가 null일 경우 예외 발생")
  void updatePrimaryItem_nullCategory_throwsException() {
    Long primaryItemId = 1L;
    PrimaryItemRequest request = new PrimaryItemRequest();
    request.setPrimaryItemName("컷트");
    request.setCategory(null);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> primaryItemCommandService.updatePrimaryItem(primaryItemId, request));

    assertEquals(ErrorCode.PRIMARY_ITEM_CATEGORY_REQUIRED, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패: 존재하지 않는 1차 상품 ID로 수정 시 예외 발생")
  void updatePrimaryItem_notFound_throwsException() {
    Long primaryItemId = 999L;
    PrimaryItemRequest request = new PrimaryItemRequest();
    request.setPrimaryItemName("컷트");
    request.setCategory(Category.SERVICE);

    when(primaryItemRepository.findById(primaryItemId)).thenReturn(java.util.Optional.empty());

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> primaryItemCommandService.updatePrimaryItem(primaryItemId, request));

    assertEquals(ErrorCode.PRIMARY_ITEM_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("성공: 1차 상품 soft delete 수행 시 deletedAt 설정")
  void deletePrimaryItem_success() {
    // given
    Long primaryItemId = 1L;
    PrimaryItem mockItem =
        PrimaryItem.builder()
            .primaryItemId(primaryItemId)
            .primaryItemName("컷트")
            .category(Category.SERVICE)
            .build();

    when(primaryItemRepository.findById(primaryItemId)).thenReturn(java.util.Optional.of(mockItem));

    // when
    primaryItemCommandService.deletePrimaryItem(primaryItemId);

    // then
    assertNotNull(mockItem.getDeletedAt());
    verify(primaryItemRepository, times(1)).save(mockItem);
  }

  @Test
  @DisplayName("실패: 존재하지 않는 1차 상품 ID로 삭제 시 예외 발생")
  void deletePrimaryItem_notFound_throwsException() {
    // given
    Long primaryItemId = 999L;
    when(primaryItemRepository.findById(primaryItemId)).thenReturn(java.util.Optional.empty());

    // when
    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> primaryItemCommandService.deletePrimaryItem(primaryItemId));

    // then
    assertEquals(ErrorCode.PRIMARY_ITEM_NOT_FOUND, exception.getErrorCode());
  }
}
