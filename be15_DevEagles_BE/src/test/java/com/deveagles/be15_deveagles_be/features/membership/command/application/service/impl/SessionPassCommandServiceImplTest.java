package com.deveagles.be15_deveagles_be.features.membership.command.application.service.impl;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.membership.command.application.dto.request.SessionPassRegistRequest;
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
    SessionPassRegistRequest request = new SessionPassRegistRequest();
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
    SessionPassRegistRequest request = new SessionPassRegistRequest();
    assertThatThrownBy(() -> service.registSessionPass(request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.ITEMS_SHOP_ID_REQUIRED.getMessage());
  }

  @Test
  @DisplayName("실패: 이름 없음")
  void registSessionPass_fail_blankName() {
    SessionPassRegistRequest request = new SessionPassRegistRequest();
    request.setShopId(1L);
    request.setSessionPassName(" ");

    assertThatThrownBy(() -> service.registSessionPass(request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.MEMBERSHIP_NAME_REQUIRED.getMessage());
  }

  @Test
  @DisplayName("실패: 가격 없음")
  void registSessionPass_fail_priceMissing() {
    SessionPassRegistRequest request = new SessionPassRegistRequest();
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
    SessionPassRegistRequest request = new SessionPassRegistRequest();
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
    SessionPassRegistRequest request = new SessionPassRegistRequest();
    request.setShopId(1L);
    request.setSessionPassName("커트");
    request.setSessionPassPrice(100000);
    request.setExpirationPeriod(30);
    request.setSession(null);

    assertThatThrownBy(() -> service.registSessionPass(request))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.MEMBERSHIP_SESSION_REQUIRED.getMessage());
  }
}
