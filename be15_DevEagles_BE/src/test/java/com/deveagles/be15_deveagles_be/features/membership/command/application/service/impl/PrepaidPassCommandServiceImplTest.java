package com.deveagles.be15_deveagles_be.features.membership.command.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.PrepaidPassRegistRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.PrepaidPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.PrepaidPassRepository;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.ShopRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PrepaidPassCommandServiceImplTest {

  private PrepaidPassRepository prepaidPassRepository;
  private ShopRepository shopRepository;
  private PrepaidPassCommandServiceImpl prepaidPassCommandService;

  @BeforeEach
  void setUp() {
    prepaidPassRepository = mock(PrepaidPassRepository.class);
    shopRepository = mock(ShopRepository.class);
    prepaidPassCommandService =
        new PrepaidPassCommandServiceImpl(prepaidPassRepository, shopRepository);
  }

  @Test
  @DisplayName("성공: 유효한 선불권 등록 요청")
  void registerPrepaidPass_success() {
    PrepaidPassRegistRequest request = new PrepaidPassRegistRequest();
    request.setShopId(1L);
    request.setPrepaidPassName("10만원권");
    request.setPrepaidPassPrice(100000);
    request.setExpirationPeriod(180);

    when(shopRepository.findById(1L)).thenReturn(Optional.of(Shop.builder().shopId(1L).build()));

    prepaidPassCommandService.registPrepaidPass(request);

    verify(prepaidPassRepository, times(1)).save(any(PrepaidPass.class));
  }

  @Test
  @DisplayName("실패: shopId가 null인 경우")
  void registerPrepaidPass_missingShopId_throwsException() {
    PrepaidPassRegistRequest request = new PrepaidPassRegistRequest();
    request.setPrepaidPassName("10만원권");
    request.setPrepaidPassPrice(100000);
    request.setExpirationPeriod(180);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> prepaidPassCommandService.registPrepaidPass(request));

    assertEquals(ErrorCode.ITEMS_SHOP_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패: shop이 존재하지 않는 경우")
  void registerPrepaidPass_shopNotFound_throwsException() {
    PrepaidPassRegistRequest request = new PrepaidPassRegistRequest();
    request.setShopId(99L);
    request.setPrepaidPassName("10만원권");
    request.setPrepaidPassPrice(100000);
    request.setExpirationPeriod(180);

    when(shopRepository.findById(99L)).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> prepaidPassCommandService.registPrepaidPass(request));

    assertEquals(ErrorCode.ITEMS_SHOP_NOT_FOUND, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패: 선불권명이 null 또는 blank인 경우")
  void registerPrepaidPass_blankName_throwsException() {
    PrepaidPassRegistRequest request = new PrepaidPassRegistRequest();
    request.setShopId(1L);
    request.setPrepaidPassName(" ");
    request.setPrepaidPassPrice(100000);
    request.setExpirationPeriod(180);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> prepaidPassCommandService.registPrepaidPass(request));

    assertEquals(ErrorCode.MEMBERSHIP_NAME_REQUIRED, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패: 가격이 null이거나 0 이하인 경우")
  void registerPrepaidPass_invalidPrice_throwsException() {
    PrepaidPassRegistRequest request = new PrepaidPassRegistRequest();
    request.setShopId(1L);
    request.setPrepaidPassName("10만원권");
    request.setPrepaidPassPrice(0);
    request.setExpirationPeriod(180);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> prepaidPassCommandService.registPrepaidPass(request));

    assertEquals(ErrorCode.MEMBERSHIP_PRICE_REQUIRED, exception.getErrorCode());
  }

  @Test
  @DisplayName("실패: 유효기간이 null이거나 0 이하인 경우")
  void registerPrepaidPass_invalidPeriod_throwsException() {
    PrepaidPassRegistRequest request = new PrepaidPassRegistRequest();
    request.setShopId(1L);
    request.setPrepaidPassName("10만원권");
    request.setPrepaidPassPrice(100000);
    request.setExpirationPeriod(0);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> prepaidPassCommandService.registPrepaidPass(request));

    assertEquals(ErrorCode.MEMBERSHIP_EXPIRATION_PERIOD_REQUIRED, exception.getErrorCode());
  }
}
