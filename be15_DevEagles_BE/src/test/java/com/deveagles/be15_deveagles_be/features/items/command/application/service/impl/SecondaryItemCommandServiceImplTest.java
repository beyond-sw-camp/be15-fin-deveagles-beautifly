package com.deveagles.be15_deveagles_be.features.items.command.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.items.command.application.dto.request.SecondaryItemRegistRequest;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.Category;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.PrimaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.SecondaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.repository.PrimaryItemRepository;
import com.deveagles.be15_deveagles_be.features.items.command.domain.repository.SecondaryItemRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SecondaryItemCommandServiceImplTest {

  private PrimaryItemRepository primaryItemRepository;
  private SecondaryItemRepository secondaryItemRepository;
  private SecondaryItemCommandServiceImpl service;

  @BeforeEach
  void setUp() {
    primaryItemRepository = mock(PrimaryItemRepository.class);
    secondaryItemRepository = mock(SecondaryItemRepository.class);
    service = new SecondaryItemCommandServiceImpl(primaryItemRepository, secondaryItemRepository);
  }

  @Test
  @DisplayName("성공: 유효한 2차 상품 등록 요청 처리")
  void registerSecondaryItem_success() {
    // given
    SecondaryItemRegistRequest request = new SecondaryItemRegistRequest();
    request.setPrimaryItemId(1L);
    request.setSecondaryItemName("펌");
    request.setSecondaryItemPrice(30000);
    request.setTimeTaken(60);

    PrimaryItem primaryItem =
        PrimaryItem.builder().primaryItemId(1L).category(Category.SERVICE).build();
    when(primaryItemRepository.findById(1L)).thenReturn(Optional.of(primaryItem));

    // when
    service.registerSecondaryItem(request);

    // then
    verify(secondaryItemRepository, times(1)).save(any(SecondaryItem.class));
  }

  @Test
  @DisplayName("실패: 요청 객체가 null인 경우 예외 발생")
  void registerSecondaryItem_nullRequest_throwsException() {
    BusinessException exception =
        assertThrows(BusinessException.class, () -> service.registerSecondaryItem(null));
    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_SECONDARY_ITEM_INPUT);
  }

  @Test
  @DisplayName("실패: 2차 상품명이 비어 있는 경우 예외 발생")
  void registerSecondaryItem_missingName_throwsException() {
    SecondaryItemRegistRequest request = new SecondaryItemRegistRequest();
    request.setPrimaryItemId(1L);
    request.setSecondaryItemName(" "); // 공백
    request.setSecondaryItemPrice(20000);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> service.registerSecondaryItem(request));
    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SECONDARY_ITEM_NAME_REQUIRED);
  }

  @Test
  @DisplayName("실패: 가격이 null인 경우 예외 발생")
  void registerSecondaryItem_missingPrice_throwsException() {
    SecondaryItemRegistRequest request = new SecondaryItemRegistRequest();
    request.setPrimaryItemId(1L);
    request.setSecondaryItemName("염색");
    request.setSecondaryItemPrice(null);

    BusinessException exception =
        assertThrows(BusinessException.class, () -> service.registerSecondaryItem(request));
    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SECONDARY_ITEM_PRICE_REQUIRED);
  }

  @Test
  @DisplayName("실패: 존재하지 않는 1차 상품 ID인 경우 예외 발생")
  void registerSecondaryItem_primaryItemNotFound_throwsException() {
    SecondaryItemRegistRequest request = new SecondaryItemRegistRequest();
    request.setPrimaryItemId(999L);
    request.setSecondaryItemName("컷");
    request.setSecondaryItemPrice(15000);

    when(primaryItemRepository.findById(999L)).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(BusinessException.class, () -> service.registerSecondaryItem(request));
    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.PRIMARY_ITEM_NOT_FOUND);
  }

  @Test
  @DisplayName("실패: 1차 상품이 SERVICE인데 시술 시간이 null인 경우 예외 발생")
  void registerSecondaryItem_missingTimeTakenForService_throwsException() {
    // given
    SecondaryItemRegistRequest request = new SecondaryItemRegistRequest();
    request.setPrimaryItemId(1L);
    request.setSecondaryItemName("드라이");
    request.setSecondaryItemPrice(10000);
    request.setTimeTaken(null); // 시간 미입력

    PrimaryItem servicePrimaryItem =
        PrimaryItem.builder().primaryItemId(1L).category(Category.SERVICE).build();

    when(primaryItemRepository.findById(1L)).thenReturn(Optional.of(servicePrimaryItem));

    // when
    BusinessException exception =
        assertThrows(BusinessException.class, () -> service.registerSecondaryItem(request));

    // then
    assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SECONDARY_ITEM_SERVICE_TIME_REQUIRED);
  }
}
