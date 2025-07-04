package com.deveagles.be15_deveagles_be.features.items.command.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.items.command.application.dto.request.PrimaryItemRequest;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.Category;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.PrimaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.SecondaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.repository.PrimaryItemRepository;
import com.deveagles.be15_deveagles_be.features.items.command.domain.repository.SecondaryItemRepository;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.ShopRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PrimaryItemCommandServiceImplTest {

  private PrimaryItemRepository primaryItemRepository;
  private ShopRepository shopRepository;
  private SecondaryItemRepository secondaryItemRepository;
  private PrimaryItemCommandServiceImpl primaryItemCommandService;

  @BeforeEach
  void setUp() {
    primaryItemRepository = mock(PrimaryItemRepository.class);
    secondaryItemRepository = mock(SecondaryItemRepository.class);
    shopRepository = mock(ShopRepository.class);
    primaryItemCommandService =
        new PrimaryItemCommandServiceImpl(
            primaryItemRepository, secondaryItemRepository, shopRepository);
  }

  @Test
  @DisplayName("성공: 유효한 1차 상품 요청 등록")
  void registerPrimaryItem_success() {
    PrimaryItemRequest request = new PrimaryItemRequest();
    request.setShopId(1L);
    request.setCategory(Category.SERVICE);
    request.setPrimaryItemName("컷트");

    when(shopRepository.findById(1L)).thenReturn(Optional.of(Shop.builder().shopId(1L).build()));

    primaryItemCommandService.registerPrimaryItem(request);

    verify(primaryItemRepository, times(1)).save(any(PrimaryItem.class));
  }

  @Test
  @DisplayName("실패: 등록 시 shopId가 null일 경우 예외 발생")
  void registerPrimaryItem_missingShopId_throwsException() {
    PrimaryItemRequest request = new PrimaryItemRequest();
    request.setPrimaryItemName("컷트");
    request.setCategory(Category.SERVICE);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> primaryItemCommandService.registerPrimaryItem(request));

    assertEquals(ErrorCode.ITEMS_SHOP_ID_REQUIRED, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패: 존재하지 않는 shopId일 경우 예외 발생")
  void registerPrimaryItem_shopNotFound_throwsException() {
    PrimaryItemRequest request = new PrimaryItemRequest();
    request.setShopId(999L);
    request.setPrimaryItemName("컷트");
    request.setCategory(Category.SERVICE);

    when(shopRepository.findById(999L)).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> primaryItemCommandService.registerPrimaryItem(request));

    assertEquals(ErrorCode.ITEMS_SHOP_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("성공: 유효한 1차 상품 수정 요청")
  void updatePrimaryItem_success() {
    Long id = 1L;
    PrimaryItemRequest request = new PrimaryItemRequest();
    request.setPrimaryItemName("펌");
    request.setCategory(Category.SERVICE);
    request.setShopId(1L);

    PrimaryItem existingItem =
        PrimaryItem.builder()
            .primaryItemId(id)
            .primaryItemName("컷트")
            .category(Category.PRODUCT)
            .build();

    when(primaryItemRepository.findById(id)).thenReturn(Optional.of(existingItem));
    when(shopRepository.existsById(1L)).thenReturn(true);

    primaryItemCommandService.updatePrimaryItem(id, request);

    assertEquals("펌", existingItem.getPrimaryItemName());
    assertEquals(Category.SERVICE, existingItem.getCategory());
    verify(primaryItemRepository, times(1)).save(existingItem);
  }

  @Test
  @DisplayName("실패: 수정 시 shopId가 null일 경우 예외 발생")
  void updatePrimaryItem_missingShopId_throwsException() {
    Long id = 1L;
    PrimaryItemRequest request = new PrimaryItemRequest();
    request.setPrimaryItemName("컷트");
    request.setCategory(Category.SERVICE);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> primaryItemCommandService.updatePrimaryItem(id, request));

    assertEquals(ErrorCode.ITEMS_SHOP_ID_REQUIRED, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패: 존재하지 않는 shopId일 경우 예외 발생")
  void updatePrimaryItem_shopNotFound_throwsException() {
    Long id = 1L;
    PrimaryItemRequest request = new PrimaryItemRequest();
    request.setShopId(99L);
    request.setPrimaryItemName("컷트");
    request.setCategory(Category.SERVICE);

    when(shopRepository.existsById(99L)).thenReturn(false);

    BusinessException exception =
        assertThrows(
            BusinessException.class,
            () -> primaryItemCommandService.updatePrimaryItem(id, request));

    assertEquals(ErrorCode.ITEMS_SHOP_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("성공: 1차 상품 삭제 수행 (2차 상품도 함께 soft delete)")
  void deletePrimaryItem_success() {
    Long id = 1L;

    // given - PrimaryItem
    PrimaryItem item =
        PrimaryItem.builder()
            .primaryItemId(id)
            .primaryItemName("컷트")
            .category(Category.SERVICE)
            .build();

    // given - SecondaryItems
    SecondaryItem secondary1 =
        SecondaryItem.builder()
            .secondaryItemId(101L)
            .primaryItemId(id)
            .secondaryItemName("남성컷")
            .build();
    SecondaryItem secondary2 =
        SecondaryItem.builder()
            .secondaryItemId(102L)
            .primaryItemId(id)
            .secondaryItemName("여성컷")
            .build();

    List<SecondaryItem> secondaryItems = List.of(secondary1, secondary2);

    // when
    when(primaryItemRepository.findById(id)).thenReturn(Optional.of(item));
    when(secondaryItemRepository.findByPrimaryItemId(id)).thenReturn(secondaryItems);

    primaryItemCommandService.deletePrimaryItem(id);

    // then
    assertNotNull(item.getDeletedAt());
    assertNotNull(secondary1.getDeletedAt());
    assertNotNull(secondary2.getDeletedAt());

    verify(primaryItemRepository).save(item);
    verify(secondaryItemRepository).save(secondary1);
    verify(secondaryItemRepository).save(secondary2);
  }

  @Test
  @DisplayName("실패: 삭제 시 존재하지 않는 ID일 경우 예외 발생")
  void deletePrimaryItem_notFound_throwsException() {
    Long id = 999L;
    when(primaryItemRepository.findById(id)).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> primaryItemCommandService.deletePrimaryItem(id));

    assertEquals(ErrorCode.PRIMARY_ITEM_NOT_FOUND, exception.getErrorCode());
  }
}
