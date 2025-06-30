package com.deveagles.be15_deveagles_be.features.membership.command.application.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.SessionPassRequest;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.SessionPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.SessionPassRepository;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.ShopRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SessionPassCommandServiceImplTest {

  private SessionPassRepository sessionPassRepository;
  private ShopRepository shopRepository;
  private SessionPassCommandServiceImpl service;

  @BeforeEach
  void setUp() {
    sessionPassRepository = mock(SessionPassRepository.class);
    shopRepository = mock(ShopRepository.class);
    service = new SessionPassCommandServiceImpl(sessionPassRepository, shopRepository);
  }

  @Test
  @DisplayName("성공: 횟수권 등록")
  void registSessionPass_success() {
    // given
    SessionPassRequest request = new SessionPassRequest();
    request.setShopId(1L);
    request.setSessionPassName("컷트 10회권");
    request.setSessionPassPrice(150000);
    request.setExpirationPeriod(90);
    request.setSession(10);
    request.setBonus(1);
    request.setDiscountRate(10);
    request.setSessionPassMemo("커트커트");

    Shop shop = Shop.builder().shopId(1L).shopName("마이샵").build();
    when(shopRepository.findById(1L)).thenReturn(Optional.of(shop));

    // when
    service.registSessionPass(request);

    // then
    verify(sessionPassRepository, times(1)).save(any(SessionPass.class));
  }

  @Test
  @DisplayName("실패: shopId 없음")
  void registSessionPass_fail_nullShopId() {
    SessionPassRequest request = new SessionPassRequest();
    assertThatThrownBy(() -> service.registSessionPass(request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.ITEMS_SHOP_ID_REQUIRED.getMessage());
  }

  @Test
  @DisplayName("실패: 이름 없음")
  void registSessionPass_fail_blankName() {
    SessionPassRequest request = new SessionPassRequest();
    request.setShopId(1L);
    request.setSessionPassName(" ");

    assertThatThrownBy(() -> service.registSessionPass(request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.MEMBERSHIP_NAME_REQUIRED.getMessage());
  }

  @Test
  @DisplayName("실패: 가격 없음")
  void registSessionPass_fail_priceMissing() {
    SessionPassRequest request = new SessionPassRequest();
    request.setShopId(1L);
    request.setSessionPassName("PT");
    request.setSessionPassPrice(null);

    assertThatThrownBy(() -> service.registSessionPass(request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.MEMBERSHIP_PRICE_REQUIRED.getMessage());
  }

  @Test
  @DisplayName("실패: 유효기간 없음")
  void registSessionPass_fail_expirationMissing() {
    SessionPassRequest request = new SessionPassRequest();
    request.setShopId(1L);
    request.setSessionPassName("PT");
    request.setSessionPassPrice(100000);
    request.setExpirationPeriod(null);

    assertThatThrownBy(() -> service.registSessionPass(request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.MEMBERSHIP_EXPIRATION_PERIOD_REQUIRED.getMessage());
  }

  @Test
  @DisplayName("실패: 횟수 없음")
  void registSessionPass_fail_sessionMissing() {
    SessionPassRequest request = new SessionPassRequest();
    request.setShopId(1L);
    request.setSessionPassName("커트");
    request.setSessionPassPrice(100000);
    request.setExpirationPeriod(30);
    request.setSession(null);

    assertThatThrownBy(() -> service.registSessionPass(request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.MEMBERSHIP_SESSION_REQUIRED.getMessage());
  }

  @Test
  @DisplayName("성공: 유효한 횟수권 수정 요청")
  void updateSessionPass_success() {
    Long id = 1L;
    SessionPassRequest request = new SessionPassRequest();
    request.setShopId(1L);
    request.setSessionPassName("여름 이벤트권");
    request.setSessionPassPrice(80000);
    request.setSession(10);
    request.setExpirationPeriod(90);
    request.setBonus(2);
    request.setDiscountRate(15);
    request.setSessionPassMemo("10회 + 2회 보너스");

    SessionPass existing = mock(SessionPass.class);

    when(shopRepository.existsById(1L)).thenReturn(true);
    when(sessionPassRepository.findById(id)).thenReturn(Optional.of(existing));

    service.updateSessionPass(id, request);

    verify(existing).updateSessionPass("여름 이벤트권", 80000, 10, 90, 2, 15, "10회 + 2회 보너스");
    verify(sessionPassRepository).save(existing);
  }

  @Test
  @DisplayName("실패: shopId가 null인 경우")
  void updateSessionPass_missingShopId() {
    SessionPassRequest request = new SessionPassRequest();
    request.setSessionPassName("이벤트권");
    request.setSessionPassPrice(80000);
    request.setExpirationPeriod(90);
    request.setSession(10);

    BusinessException ex =
        assertThrows(BusinessException.class, () -> service.updateSessionPass(1L, request));

    assertEquals(ErrorCode.ITEMS_SHOP_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  @DisplayName("실패: 이름이 null 또는 빈 문자열")
  void updateSessionPass_blankName() {
    SessionPassRequest request = new SessionPassRequest();
    request.setShopId(1L);
    request.setSessionPassName(" ");
    request.setSessionPassPrice(80000);
    request.setExpirationPeriod(90);
    request.setSession(10);

    BusinessException ex =
        assertThrows(BusinessException.class, () -> service.updateSessionPass(1L, request));

    assertEquals(ErrorCode.MEMBERSHIP_NAME_REQUIRED, ex.getErrorCode());
  }

  @Test
  @DisplayName("실패: 가격이 null 또는 0 이하")
  void updateSessionPass_invalidPrice() {
    SessionPassRequest request = new SessionPassRequest();
    request.setShopId(1L);
    request.setSessionPassName("이벤트권");
    request.setSessionPassPrice(0);
    request.setExpirationPeriod(90);
    request.setSession(10);

    BusinessException ex =
        assertThrows(BusinessException.class, () -> service.updateSessionPass(1L, request));

    assertEquals(ErrorCode.MEMBERSHIP_PRICE_REQUIRED, ex.getErrorCode());
  }

  @Test
  @DisplayName("실패: 유효기간이 null 또는 0 이하")
  void updateSessionPass_invalidExpirationPeriod() {
    SessionPassRequest request = new SessionPassRequest();
    request.setShopId(1L);
    request.setSessionPassName("이벤트권");
    request.setSessionPassPrice(80000);
    request.setExpirationPeriod(0);
    request.setSession(10);

    BusinessException ex =
        assertThrows(BusinessException.class, () -> service.updateSessionPass(1L, request));

    assertEquals(ErrorCode.MEMBERSHIP_EXPIRATION_PERIOD_REQUIRED, ex.getErrorCode());
  }

  @Test
  @DisplayName("실패: 존재하지 않는 shopId")
  void updateSessionPass_shopNotFound() {
    SessionPassRequest request = new SessionPassRequest();
    request.setShopId(99L);
    request.setSessionPassName("이벤트권");
    request.setSessionPassPrice(80000);
    request.setExpirationPeriod(90);
    request.setSession(10);

    when(shopRepository.existsById(99L)).thenReturn(false);

    BusinessException ex =
        assertThrows(BusinessException.class, () -> service.updateSessionPass(1L, request));

    assertEquals(ErrorCode.ITEMS_SHOP_NOT_FOUND, ex.getErrorCode());
  }

  @Test
  @DisplayName("실패: session이 null 또는 0 이하")
  void updateSessionPass_invalidSession() {
    SessionPassRequest request = new SessionPassRequest();
    request.setShopId(1L);
    request.setSessionPassName("이름");
    request.setSessionPassPrice(80000);
    request.setExpirationPeriod(90);
    request.setSession(0); // 또는 null

    BusinessException ex =
        assertThrows(BusinessException.class, () -> service.updateSessionPass(1L, request));

    assertEquals(ErrorCode.MEMBERSHIP_SESSION_REQUIRED, ex.getErrorCode());
  }

  @Test
  @DisplayName("실패: 존재하지 않는 sessionPass ID")
  void updateSessionPass_notFound() {
    SessionPassRequest request = new SessionPassRequest();
    request.setShopId(1L);
    request.setSessionPassName("이벤트권");
    request.setSessionPassPrice(80000);
    request.setExpirationPeriod(90);
    request.setSession(10);

    when(shopRepository.existsById(1L)).thenReturn(true);
    when(sessionPassRepository.findById(1L)).thenReturn(Optional.empty());

    BusinessException ex =
        assertThrows(BusinessException.class, () -> service.updateSessionPass(1L, request));

    assertEquals(ErrorCode.SESSIONPASS_NOT_FOUND, ex.getErrorCode());
  }
}
