package com.deveagles.be15_deveagles_be.features.shops.command.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.service.ReservationSettingInitializer;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.ShopCreateRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.ValidBizNumberRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.response.GetShopResponse;
import com.deveagles.be15_deveagles_be.features.shops.command.application.service.IndustryRepository;
import com.deveagles.be15_deveagles_be.features.shops.command.application.service.ShopCommandServiceImpl;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Industry;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.SNS;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.SNSType;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.ShopRepository;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.SnsRepository;
import java.util.List;
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

  @Mock private SnsRepository snsRepository;

  @Mock private ReservationSettingInitializer reservationSettingInitializer;

  private ShopCommandServiceImpl shopCommandService;

  @BeforeEach
  void setUp() {
    shopCommandService =
        new ShopCommandServiceImpl(
            shopRepository, industryRepository, snsRepository, reservationSettingInitializer);
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
    Shop result = shopCommandService.shopRegist(request);

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
    Boolean result = shopCommandService.validCheckBizNumber(request);

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
    Boolean result = shopCommandService.validCheckBizNumber(request);

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
    shopCommandService.patchOwnerId(shop, ownerId);

    // then
    assertEquals(ownerId, shop.getOwnerId());
    Mockito.verify(shopRepository).save(shop);
  }

  @Test
  @DisplayName("validateShopExists: 존재하는 shopId일 경우 예외 없이 통과")
  void 존재하는_shopId_예외없음() {
    // given
    Long validShopId = 1L;
    Mockito.when(shopRepository.existsById(validShopId)).thenReturn(true);

    // when & then
    assertDoesNotThrow(() -> shopCommandService.validateShopExists(validShopId));
  }

  @Test
  @DisplayName("validateShopExists: 존재하지 않는 shopId일 경우 예외 발생")
  void 존재하지않는_shopId_예외발생() {
    // given
    Long invalidShopId = 999L;
    Mockito.when(shopRepository.existsById(invalidShopId)).thenReturn(false);

    // when & then
    BusinessException exception =
        assertThrows(
            BusinessException.class, () -> shopCommandService.validateShopExists(invalidShopId));

    assertEquals(ErrorCode.SHOP_NOT_FOUNT, exception.getErrorCode());
  }

  @Test
  void getShop_정상조회() {
    // given
    Long shopId = 1L;

    Shop shop =
        Shop.builder()
            .shopId(shopId)
            .shopName("테스트샵")
            .address("서울시 강남구")
            .detailAddress("2층")
            .phoneNumber("010-1234-5678")
            .businessNumber("123-45-67890")
            .shopDescription("설명")
            .industryId(10L)
            .build();

    List<Industry> industryList = List.of(new Industry(10L, "미용"), new Industry(20L, "네일"));

    List<SNS> snsList =
        List.of(
            new SNS(1L, "https://insta.com/beauty", 1L, SNSType.INSTA),
            new SNS(2L, "https://pf.kakao.com/_test", 1L, SNSType.ETC));

    // when
    Mockito.when(shopRepository.findByShopId(shopId)).thenReturn(Optional.of(shop));
    Mockito.when(industryRepository.findAll()).thenReturn(industryList);
    Mockito.when(snsRepository.findByShopId(shopId)).thenReturn(snsList);

    GetShopResponse response = shopCommandService.getShop(shopId);

    // then
    assertThat(response.getShopName()).isEqualTo("테스트샵");
    assertThat(response.getAddress()).isEqualTo("서울시 강남구");
    assertThat(response.getIndustryList()).hasSize(2);
    assertThat(response.getSnsList()).hasSize(2);
  }
}
