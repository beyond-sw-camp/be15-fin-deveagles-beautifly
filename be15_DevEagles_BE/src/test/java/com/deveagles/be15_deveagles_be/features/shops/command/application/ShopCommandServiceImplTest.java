package com.deveagles.be15_deveagles_be.features.shops.command.application;

import static org.junit.jupiter.api.Assertions.*;

import com.deveagles.be15_deveagles_be.features.schedules.command.application.service.ReservationSettingInitializer;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.ShopCreateRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.ValidBizNumberRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.service.IndustryRepository;
import com.deveagles.be15_deveagles_be.features.shops.command.application.service.ShopCommandServiceImpl;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.ShopRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("ShopCommandService 단위 테스트")
public class ShopCommandServiceImplTest {

  @Mock private ShopRepository shopRepository;

  @Mock private IndustryRepository industryRepository;

  @Mock private ReservationSettingInitializer reservationSettingInitializer;

  private ShopCommandServiceImpl service;

  @BeforeEach
  void setUp() {
    service =
        new ShopCommandServiceImpl(
            shopRepository, industryRepository, reservationSettingInitializer);
  }

  @Test
  @DisplayName("shopRegist: 신규 매장 등록 시 정상적으로 수행되고 예약 설정도 초기화된다")
  void shopRegist_정상_등록_테스트() {
    // given
    ShopCreateRequest request =
        ShopCreateRequest.builder()
            .shopName("디브이헤어")
            .address("서울특별시 강남구 테헤란로")
            .detailAddress("101호")
            .industryId(1L)
            .businessNumber("1234567890")
            .phoneNumber("01012345678")
            .description("프리미엄 헤어샵")
            .build();

    Shop expectedShop =
        Shop.builder()
            .shopId(1L)
            .shopName("디브이헤어")
            .address("서울특별시 강남구 테헤란로")
            .detailAddress("101호")
            .industryId(1L)
            .businessNumber("1234567890")
            .phoneNumber("01012345678")
            .shopDescription("프리미엄 헤어샵")
            .build();

    Mockito.when(shopRepository.save(Mockito.any(Shop.class))).thenReturn(expectedShop);

    // when
    Shop result = service.shopRegist(request);

    // then
    assertEquals("디브이헤어", result.getShopName());
    assertEquals("1234567890", result.getBusinessNumber());
    assertEquals("프리미엄 헤어샵", result.getShopDescription());

    Mockito.verify(reservationSettingInitializer).initDefault(1L);
  }

  @Test
  @DisplayName("validCheckBizNumber: 중복 사업자번호이면 false 반환")
  void 중복된_사업자번호이면_FALSE반환() {
    // given
    String bizNumber = "1234567890";
    ValidBizNumberRequest request = new ValidBizNumberRequest(bizNumber);
    Mockito.when(shopRepository.findByBusinessNumber(bizNumber))
        .thenReturn(Optional.of(Shop.builder().businessNumber(bizNumber).build()));

    // when
    Boolean result = service.validCheckBizNumber(request);

    // then
    assertFalse(result);
  }

  @Test
  @DisplayName("validCheckBizNumber: 사용 가능한 사업자번호이면 true 반환")
  void 사용가능한_사업자번호이면_TRUE반환() {
    // given
    String bizNumber = "1112233333";
    ValidBizNumberRequest request = new ValidBizNumberRequest(bizNumber);
    Mockito.when(shopRepository.findByBusinessNumber(bizNumber)).thenReturn(Optional.empty());

    // when
    Boolean result = service.validCheckBizNumber(request);

    // then
    assertTrue(result);
  }

  @Test
  @DisplayName("patchOwnerId: 매장에 ownerId가 정상적으로 반영된다")
  void patchOwnerId_테스트() {
    // given
    Shop shop = Shop.builder().shopName("디브이헤어").build();
    Long ownerId = 999L;

    // when
    service.patchOwnerId(shop, ownerId);

    // then
    assertEquals(ownerId, shop.getOwnerId());
    Mockito.verify(shopRepository).save(shop);
  }
}
