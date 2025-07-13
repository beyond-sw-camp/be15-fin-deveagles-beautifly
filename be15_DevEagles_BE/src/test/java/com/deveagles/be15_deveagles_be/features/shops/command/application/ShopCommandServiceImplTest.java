package com.deveagles.be15_deveagles_be.features.shops.command.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.schedules.command.application.service.ReservationSettingInitializer;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.PutShopRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.ShopCreateRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.SnsRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.request.ValidBizNumberRequest;
import com.deveagles.be15_deveagles_be.features.shops.command.application.dto.response.GetShopResponse;
import com.deveagles.be15_deveagles_be.features.shops.command.application.service.ShopCommandServiceImpl;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Industry;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.SNS;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.SNSType;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.IndustryRepository;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.ShopRepository;
import com.deveagles.be15_deveagles_be.features.shops.command.repository.SnsRepository;
import java.util.Collections;
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

    when(shopRepository.save(Mockito.any(Shop.class))).thenReturn(expectedShop);

    // when
    Shop result = shopCommandService.shopRegist(request);

    // then
    assertEquals("디브이헤어", result.getShopName());
    assertEquals("1234567890", result.getBusinessNumber());
    assertEquals("프리미엄 헤어샵", result.getShopDescription());

    verify(reservationSettingInitializer).initDefault(1L);
  }

  @Test
  @DisplayName("validCheckBizNumber: 중복 사업자번호이면 false 반환")
  void 중복된_사업자번호이면_FALSE반환() {
    // given
    String bizNumber = "1234567890";
    ValidBizNumberRequest request = new ValidBizNumberRequest(bizNumber);
    when(shopRepository.findByBusinessNumber(bizNumber))
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
    when(shopRepository.findByBusinessNumber(bizNumber)).thenReturn(Optional.empty());

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
    verify(shopRepository).save(shop);
  }

  @Test
  @DisplayName("validateShopExists: 존재하는 shopId일 경우 예외 없이 통과")
  void 존재하는_shopId_예외없음() {
    // given
    Long validShopId = 1L;
    when(shopRepository.existsById(validShopId)).thenReturn(true);

    // when & then
    assertDoesNotThrow(() -> shopCommandService.validateShopExists(validShopId));
  }

  @Test
  @DisplayName("validateShopExists: 존재하지 않는 shopId일 경우 예외 발생")
  void 존재하지않는_shopId_예외발생() {
    // given
    Long invalidShopId = 999L;
    when(shopRepository.existsById(invalidShopId)).thenReturn(false);

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
    when(shopRepository.findByShopId(shopId)).thenReturn(Optional.of(shop));
    when(industryRepository.findAll()).thenReturn(industryList);
    when(snsRepository.findByShopId(shopId)).thenReturn(snsList);

    GetShopResponse response = shopCommandService.getShop(shopId);

    // then
    assertThat(response.getShopName()).isEqualTo("테스트샵");
    assertThat(response.getAddress()).isEqualTo("서울시 강남구");
    assertThat(response.getIndustryList()).hasSize(2);
    assertThat(response.getSnsList()).hasSize(2);
  }

  @Test
  @DisplayName("putShop - 매장 정보 수정 및 SNS 처리 성공")
  void putShop_성공() {
    // given
    Long shopId = 1L;
    Shop existingShop =
        Shop.builder()
            .shopId(shopId)
            .shopName("OldName")
            .address("Old Address")
            .detailAddress("Old Detail")
            .industryId(100L)
            .phoneNumber("010-0000-0000")
            .businessNumber("1234567890")
            .shopDescription("Old Description")
            .build();

    when(shopRepository.findByShopId(shopId)).thenReturn(Optional.of(existingShop));

    List<Long> deletedSnsIds = List.of(11L, 12L);
    List<SnsRequest> snsList =
        List.of(
            new SnsRequest(13L, "INSTA", "https://insta.com/new1"),
            new SnsRequest(null, "ETC", "https://kakao.com/new2"));

    PutShopRequest request =
        new PutShopRequest(
            "NewName",
            "New Address",
            "New Detail",
            200L,
            "010-1234-5678",
            "0987654321",
            "New Description",
            snsList,
            deletedSnsIds);

    SNS existingSns = SNS.builder().snsId(13L).build();
    when(snsRepository.findBySnsIdIn(List.of(13L))).thenReturn(List.of(existingSns));

    // when
    shopCommandService.putShop(shopId, request);

    // then
    // 상점 필드 변경 확인
    assertThat(existingShop.getShopName()).isEqualTo("NewName");
    assertThat(existingShop.getAddress()).isEqualTo("New Address");
    assertThat(existingShop.getDetailAddress()).isEqualTo("New Detail");
    assertThat(existingShop.getIndustryId()).isEqualTo(200L);
    assertThat(existingShop.getPhoneNumber()).isEqualTo("010-1234-5678");
    assertThat(existingShop.getBusinessNumber()).isEqualTo("0987654321");
    assertThat(existingShop.getShopDescription()).isEqualTo("New Description");

    // 삭제 확인
    verify(snsRepository).deleteBySnsIdIn(deletedSnsIds);

    // SNS 저장 확인
    verify(snsRepository, times(2)).save(any(SNS.class));
  }

  @Test
  @DisplayName("putShop - 존재하지 않는 상점 예외")
  void putShop_상점없음() {
    // given
    when(shopRepository.findByShopId(anyLong())).thenReturn(Optional.empty());

    PutShopRequest dummyRequest =
        new PutShopRequest(
            "name",
            "addr",
            "detail",
            1L,
            "010",
            "biz",
            "desc",
            Collections.emptyList(),
            Collections.emptyList());

    // when & then
    assertThatThrownBy(() -> shopCommandService.putShop(1L, dummyRequest))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining(ErrorCode.SHOP_NOT_FOUNT.getMessage());
  }

  @Test
  @DisplayName("putShop - SNS 삭제 및 수정/추가 처리 확인")
  void putShop_Sns삭제_수정_추가() {
    // given
    Long shopId = 1L;

    Shop existingShop =
        Shop.builder()
            .shopId(shopId)
            .shopName("SameName")
            .address("Same Address")
            .detailAddress("Same Detail")
            .industryId(1L)
            .phoneNumber("010-1111-2222")
            .businessNumber("1111222233")
            .shopDescription("Same Description")
            .build();

    when(shopRepository.findByShopId(shopId)).thenReturn(Optional.of(existingShop));

    // 삭제 대상 SNS ID
    List<Long> deletedSnsIds = List.of(101L, 102L);

    // SNS 수정 및 추가 요청
    SnsRequest modifySns = new SnsRequest(201L, "INSTA", "https://instagram.com/updated");
    SnsRequest newSns = new SnsRequest(null, "ETC", "https://kakao.com/new");

    List<SnsRequest> snsList = List.of(modifySns, newSns);

    // 기존 SNS DB
    SNS existingSns = spy(SNS.builder().snsId(201L).shopId(shopId).build());
    when(snsRepository.findBySnsIdIn(List.of(201L))).thenReturn(List.of(existingSns));

    PutShopRequest request =
        new PutShopRequest(
            "SameName",
            "Same Address",
            "Same Detail",
            1L,
            "010-1111-2222",
            "1111222233",
            "Same Description",
            snsList,
            deletedSnsIds);

    // when
    shopCommandService.putShop(shopId, request);

    // then
    // 삭제된 SNS ID로 deleteBySnsIdIn 호출 확인
    verify(snsRepository).deleteBySnsIdIn(deletedSnsIds);

    // 기존 SNS 수정(setSns 호출 확인)
    verify(existingSns).setSns(SNSType.INSTA, "https://instagram.com/updated");

    // SNS save가 2회 (수정된 existing + 새 SNS)
    verify(snsRepository, times(2)).save(any(SNS.class));
  }

  @Test
  @DisplayName("updateReservationTerm: 예약 단위 시간이 정상적으로 업데이트된다")
  void updateReservationTerm_정상() {
    // given
    Long shopId = 1L;
    Integer term = 10;

    Shop shop = Shop.builder().shopId(shopId).reservationTerm(10).build();

    when(shopRepository.findByShopId(shopId)).thenReturn(Optional.of(shop));

    // when
    shopCommandService.updateReservationTerm(shopId, term);

    // then
    assertThat(shop.getReservationTerm()).isEqualTo(term);
    verify(shopRepository).save(shop);
  }
}
