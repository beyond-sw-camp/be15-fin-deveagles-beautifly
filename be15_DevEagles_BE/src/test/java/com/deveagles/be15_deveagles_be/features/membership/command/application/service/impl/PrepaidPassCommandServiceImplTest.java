package com.deveagles.be15_deveagles_be.features.membership.command.application.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.PrepaidPassRequest;
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
    PrepaidPassRequest request = new PrepaidPassRequest();
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
    PrepaidPassRequest request = new PrepaidPassRequest();
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
    PrepaidPassRequest request = new PrepaidPassRequest();
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
    PrepaidPassRequest request = new PrepaidPassRequest();
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
    PrepaidPassRequest request = new PrepaidPassRequest();
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
    PrepaidPassRequest request = new PrepaidPassRequest();
    request.setShopId(1L);
    request.setPrepaidPassName("10만원권");
    request.setPrepaidPassPrice(100000);
    request.setExpirationPeriod(0);

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> prepaidPassCommandService.registPrepaidPass(request));

    assertEquals(ErrorCode.MEMBERSHIP_EXPIRATION_PERIOD_REQUIRED, exception.getErrorCode());
  }

  @Test
  @DisplayName("성공: 유효한 선불권 수정 요청")
  void updatePrepaidPass_success() {
    Long id = 1L;
    PrepaidPassRequest request = new PrepaidPassRequest();
    request.setShopId(1L);
    request.setPrepaidPassName("여름 할인권");
    request.setPrepaidPassPrice(50000);
    request.setExpirationPeriod(90);
    request.setBonus(5);
    request.setDiscountRate(10);
    request.setPrepaidPassMemo("이벤트 중");

    PrepaidPass existing = mock(PrepaidPass.class);

    when(shopRepository.existsById(1L)).thenReturn(true);
    when(prepaidPassRepository.findById(id)).thenReturn(Optional.of(existing));

    prepaidPassCommandService.updatePrepaidPass(id, request);

    verify(existing).updatePrepaidPass("여름 할인권", 50000, 90, 5, 10, "이벤트 중");
    verify(prepaidPassRepository).save(existing);
  }

  @Test
  @DisplayName("실패: shopId가 null인 경우")
  void updatePrepaidPass_missingShopId() {
    PrepaidPassRequest request = new PrepaidPassRequest();
    request.setPrepaidPassName("여름 할인권");
    request.setPrepaidPassPrice(50000);
    request.setExpirationPeriod(90);

    BusinessException ex =
        assertThrows(
            BusinessException.class,
            () -> prepaidPassCommandService.updatePrepaidPass(1L, request));

    assertEquals(ErrorCode.ITEMS_SHOP_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  @DisplayName("실패: 이름이 null 또는 빈 문자열")
  void updatePrepaidPass_blankName() {
    PrepaidPassRequest request = new PrepaidPassRequest();
    request.setShopId(1L);
    request.setPrepaidPassName(" ");
    request.setPrepaidPassPrice(50000);
    request.setExpirationPeriod(90);

    BusinessException ex =
        assertThrows(
            BusinessException.class,
            () -> prepaidPassCommandService.updatePrepaidPass(1L, request));

    assertEquals(ErrorCode.MEMBERSHIP_NAME_REQUIRED, ex.getErrorCode());
  }

  @Test
  @DisplayName("실패: 가격이 null 또는 0 이하")
  void updatePrepaidPass_invalidPrice() {
    PrepaidPassRequest request = new PrepaidPassRequest();
    request.setShopId(1L);
    request.setPrepaidPassName("이름");
    request.setPrepaidPassPrice(0); // 또는 null
    request.setExpirationPeriod(90);

    BusinessException ex =
        assertThrows(
            BusinessException.class,
            () -> prepaidPassCommandService.updatePrepaidPass(1L, request));

    assertEquals(ErrorCode.MEMBERSHIP_PRICE_REQUIRED, ex.getErrorCode());
  }

  @Test
  @DisplayName("실패: 유효기간이 null 또는 0 이하")
  void updatePrepaidPass_invalidExpirationPeriod() {
    PrepaidPassRequest request = new PrepaidPassRequest();
    request.setShopId(1L);
    request.setPrepaidPassName("이름");
    request.setPrepaidPassPrice(50000);
    request.setExpirationPeriod(0); // 또는 null

    BusinessException ex =
        assertThrows(
            BusinessException.class,
            () -> prepaidPassCommandService.updatePrepaidPass(1L, request));

    assertEquals(ErrorCode.MEMBERSHIP_EXPIRATION_PERIOD_REQUIRED, ex.getErrorCode());
  }

  @Test
  @DisplayName("실패: 존재하지 않는 shopId")
  void updatePrepaidPass_shopNotFound() {
    PrepaidPassRequest request = new PrepaidPassRequest();
    request.setShopId(99L);
    request.setPrepaidPassName("이름");
    request.setPrepaidPassPrice(50000);
    request.setExpirationPeriod(90);

    when(shopRepository.existsById(99L)).thenReturn(false);

    BusinessException ex =
        assertThrows(
            BusinessException.class,
            () -> prepaidPassCommandService.updatePrepaidPass(1L, request));

    assertEquals(ErrorCode.ITEMS_SHOP_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  @DisplayName("실패: 존재하지 않는 선불권 ID")
  void updatePrepaidPass_passNotFound() {
    PrepaidPassRequest request = new PrepaidPassRequest();
    request.setShopId(1L);
    request.setPrepaidPassName("이름");
    request.setPrepaidPassPrice(50000);
    request.setExpirationPeriod(90);

    when(shopRepository.existsById(1L)).thenReturn(true);
    when(prepaidPassRepository.findById(1L)).thenReturn(Optional.empty());

    BusinessException ex =
        assertThrows(
            BusinessException.class,
            () -> prepaidPassCommandService.updatePrepaidPass(1L, request));

    assertEquals(ErrorCode.PREPAIDPASS_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  @DisplayName("성공: 선불권 삭제 수행")
  void deletePrepaidPass_success() {
    Long id = 1L;
    PrepaidPass pass =
        PrepaidPass.builder()
            .prepaidPassId(id)
            .prepaidPassName("여름 이벤트권")
            .prepaidPassPrice(50000)
            .expirationPeriod(90)
            .build();

    when(prepaidPassRepository.findById(id)).thenReturn(Optional.of(pass));

    prepaidPassCommandService.deletePrepaidPass(id);

    assertNotNull(pass.getDeletedAt());
    verify(prepaidPassRepository).save(pass);
  }

  @Test
  @DisplayName("실패: 삭제 시 존재하지 않는 ID일 경우 예외 발생")
  void deletePrepaidPass_notFound_throwsException() {
    Long id = 999L;
    when(prepaidPassRepository.findById(id)).thenReturn(Optional.empty());

    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> prepaidPassCommandService.deletePrepaidPass(id));

    assertEquals(ErrorCode.PREPAIDPASS_NOT_FOUND, exception.getErrorCode());
  }
}
