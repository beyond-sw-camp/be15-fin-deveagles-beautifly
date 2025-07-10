package com.deveagles.be15_deveagles_be.features.coupons.application.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.coupons.common.CouponDto;
import com.deveagles.be15_deveagles_be.features.coupons.domain.entity.Coupon;
import com.deveagles.be15_deveagles_be.features.coupons.domain.repository.CouponQueryRepository;
import com.deveagles.be15_deveagles_be.features.coupons.infrastructure.repository.CouponJpaRepository;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response.CouponResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponQueryService 단위 테스트")
class CouponSearchQueryServiceImplTest {

  @Mock private CouponJpaRepository couponJpaRepository;
  @Mock private CouponQueryRepository couponQueryRepository;

  @InjectMocks private CouponQueryServiceImpl couponQueryService;

  private Coupon activeCoupon;
  private Coupon inactiveCoupon;
  private Coupon deletedCoupon;
  private List<Coupon> couponList;

  @BeforeEach
  void setUp() {
    activeCoupon =
        Coupon.builder()
            .id(1L)
            .couponCode("CP241201ABCD1234")
            .couponTitle("활성 쿠폰")
            .shopId(1L)
            .staffId(1L)
            .primaryItemId(100L)
            .secondaryItemId(200L)
            .discountRate(10)
            .expirationDate(LocalDate.now().plusDays(30))
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .build();

    inactiveCoupon =
        Coupon.builder()
            .id(2L)
            .couponCode("CP241201EFGH5678")
            .couponTitle("비활성 쿠폰")
            .shopId(2L)
            .staffId(2L)
            .primaryItemId(101L)
            .discountRate(15)
            .expirationDate(LocalDate.now().plusDays(15))
            .isActive(false)
            .createdAt(LocalDateTime.now().minusDays(1))
            .build();

    deletedCoupon =
        Coupon.builder()
            .id(3L)
            .couponCode("CP241201IJKL9012")
            .couponTitle("삭제된 쿠폰")
            .shopId(1L)
            .staffId(1L)
            .primaryItemId(100L)
            .discountRate(20)
            .expirationDate(LocalDate.now().plusDays(10))
            .isActive(false)
            .createdAt(LocalDateTime.now().minusDays(2))
            .deletedAt(LocalDateTime.now().minusHours(1))
            .build();

    couponList = Arrays.asList(activeCoupon, inactiveCoupon);
  }

  private CouponDto createCouponDto(
      Long id,
      String couponCode,
      String couponTitle,
      Long shopId,
      Integer discountRate,
      LocalDate expirationDate,
      Boolean isActive,
      LocalDateTime createdAt,
      Long staffId,
      String staffName,
      Long primaryItemId,
      String primaryItemName,
      String primaryItemCategory,
      Long secondaryItemId,
      String secondaryItemName) {
    return new CouponDto(
        id,
        couponCode,
        couponTitle,
        shopId,
        discountRate,
        expirationDate,
        isActive,
        createdAt,
        staffId,
        staffName,
        primaryItemId,
        primaryItemName,
        primaryItemCategory,
        secondaryItemId,
        secondaryItemName);
  }

  @Test
  @DisplayName("쿠폰 ID로 조회 성공")
  void getCouponById_성공() {
    // Given
    given(couponJpaRepository.findByIdAndShopIdAndDeletedAtIsNull(1L, 1L))
        .willReturn(Optional.of(activeCoupon));

    // When
    Optional<CouponResponse> result = couponQueryService.getCouponById(1L, 1L);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(1L);
    assertThat(result.get().getCouponCode()).isEqualTo("CP241201ABCD1234");
    assertThat(result.get().getCouponTitle()).isEqualTo("활성 쿠폰");

    then(couponJpaRepository).should(times(1)).findByIdAndShopIdAndDeletedAtIsNull(1L, 1L);
  }

  @Test
  @DisplayName("쿠폰 ID로 조회 실패 - 존재하지 않는 쿠폰")
  void getCouponById_존재하지않는쿠폰_실패() {
    // Given
    given(couponJpaRepository.findByIdAndShopIdAndDeletedAtIsNull(999L, 1L))
        .willReturn(Optional.empty());

    // When
    Optional<CouponResponse> result = couponQueryService.getCouponById(999L, 1L);

    // Then
    assertThat(result).isEmpty();

    then(couponJpaRepository).should(times(1)).findByIdAndShopIdAndDeletedAtIsNull(999L, 1L);
  }

  @Test
  @DisplayName("쿠폰 ID로 조회 실패 - 삭제된 쿠폰")
  void getCouponById_삭제된쿠폰_실패() {
    // Given
    given(couponJpaRepository.findByIdAndShopIdAndDeletedAtIsNull(3L, 1L))
        .willReturn(Optional.empty());

    // When
    Optional<CouponResponse> result = couponQueryService.getCouponById(3L, 1L);

    // Then
    assertThat(result).isEmpty();

    then(couponJpaRepository).should(times(1)).findByIdAndShopIdAndDeletedAtIsNull(3L, 1L);
  }

  @Test
  @DisplayName("쿠폰 코드로 조회 성공")
  void getCouponByCode_성공() {
    // Given
    String couponCode = "CP241201ABCD1234";
    given(couponJpaRepository.findByCouponCodeAndShopIdAndDeletedAtIsNull(couponCode, 1L))
        .willReturn(Optional.of(activeCoupon));

    // When
    Optional<CouponResponse> result = couponQueryService.getCouponByCode(couponCode, 1L);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getCouponCode()).isEqualTo(couponCode);

    then(couponJpaRepository)
        .should(times(1))
        .findByCouponCodeAndShopIdAndDeletedAtIsNull(couponCode, 1L);
  }

  @Test
  @DisplayName("쿠폰 코드로 조회 실패 - 존재하지 않는 코드")
  void getCouponByCode_존재하지않는코드_실패() {
    // Given
    String nonExistentCode = "NONEXISTENT";
    given(couponJpaRepository.findByCouponCodeAndShopIdAndDeletedAtIsNull(nonExistentCode, 1L))
        .willReturn(Optional.empty());

    // When
    Optional<CouponResponse> result = couponQueryService.getCouponByCode(nonExistentCode, 1L);

    // Then
    assertThat(result).isEmpty();

    then(couponJpaRepository)
        .should(times(1))
        .findByCouponCodeAndShopIdAndDeletedAtIsNull(nonExistentCode, 1L);
  }

  @Test
  @DisplayName("쿠폰 검색 성공 - 전체 조회")
  void searchCoupons_전체조회_성공() {
    // Given
    CouponSearchQuery query =
        CouponSearchQuery.builder()
            .page(0)
            .size(10)
            .sortBy("createdAt")
            .sortDirection("desc")
            .build();

    Pageable pageable = PageRequest.of(0, 10);
    List<CouponDto> couponDtoList =
        Arrays.asList(
            createCouponDto(
                1L,
                "CP1",
                "Title1",
                1L,
                10,
                LocalDate.now().plusDays(10),
                true,
                LocalDateTime.now(),
                101L,
                "Designer A",
                201L,
                "Primary A",
                "Cat A",
                301L,
                "Secondary A"),
            createCouponDto(
                2L,
                "CP2",
                "Title2",
                1L,
                20,
                LocalDate.now().plusDays(20),
                false,
                LocalDateTime.now(),
                102L,
                "Designer B",
                202L,
                "Primary B",
                "Cat B",
                null,
                null));
    Page<CouponDto> couponPage = new PageImpl<>(couponDtoList, pageable, couponDtoList.size());

    given(couponQueryRepository.searchCoupons(eq(query), any(Pageable.class)))
        .willReturn(couponPage);

    // When
    PagedResult<CouponResponse> result = couponQueryService.searchCoupons(query);

    // Then
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getPagination().getTotalItems()).isEqualTo(2);
    assertThat(result.getContent().get(0).getDesignerInfo().getStaffName()).isEqualTo("Designer A");
    assertThat(result.getContent().get(0).getPrimaryItemInfo().getName()).isEqualTo("Primary A");
    assertThat(result.getContent().get(0).getPrimaryItemInfo().getCategory()).isEqualTo("Cat A");
    assertThat(result.getContent().get(0).getSecondaryItemInfo().getName())
        .isEqualTo("Secondary A");

    then(couponQueryRepository).should(times(1)).searchCoupons(eq(query), any(Pageable.class));
  }

  @Test
  @DisplayName("쿠폰 검색 성공 - 활성 쿠폰만")
  void searchCoupons_활성쿠폰만_성공() {
    // Given
    CouponSearchQuery query =
        CouponSearchQuery.builder()
            .isActive(true)
            .page(0)
            .size(10)
            .sortBy("createdAt")
            .sortDirection("desc")
            .build();

    Pageable pageable = PageRequest.of(0, 10);
    List<CouponDto> activeCouponDtoList =
        Arrays.asList(
            createCouponDto(
                1L,
                "CP1",
                "Title1",
                1L,
                10,
                LocalDate.now().plusDays(10),
                true,
                LocalDateTime.now(),
                101L,
                "Designer A",
                201L,
                "Primary A",
                "Cat A",
                301L,
                "Secondary A"));
    Page<CouponDto> couponPage =
        new PageImpl<>(activeCouponDtoList, pageable, activeCouponDtoList.size());

    given(couponQueryRepository.searchCoupons(eq(query), any(Pageable.class)))
        .willReturn(couponPage);

    // When
    PagedResult<CouponResponse> result = couponQueryService.searchCoupons(query);

    // Then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getIsActive()).isTrue();
    assertThat(result.getContent().get(0).getDesignerInfo().getStaffName()).isEqualTo("Designer A");

    then(couponQueryRepository).should(times(1)).searchCoupons(eq(query), any(Pageable.class));
  }

  @Test
  @DisplayName("쿠폰 검색 성공 - 매장별 조회")
  void searchCoupons_매장별조회_성공() {
    // Given
    CouponSearchQuery query =
        CouponSearchQuery.builder()
            .shopId(1L)
            .page(0)
            .size(10)
            .sortBy("createdAt")
            .sortDirection("desc")
            .build();

    Pageable pageable = PageRequest.of(0, 10);
    List<CouponDto> shopCouponDtoList =
        Arrays.asList(
            createCouponDto(
                1L,
                "CP1",
                "Title1",
                1L,
                10,
                LocalDate.now().plusDays(10),
                true,
                LocalDateTime.now(),
                101L,
                "Designer A",
                201L,
                "Primary A",
                "Cat A",
                301L,
                "Secondary A"));
    Page<CouponDto> couponPage =
        new PageImpl<>(shopCouponDtoList, pageable, shopCouponDtoList.size());

    given(couponQueryRepository.searchCoupons(eq(query), any(Pageable.class)))
        .willReturn(couponPage);

    // When
    PagedResult<CouponResponse> result = couponQueryService.searchCoupons(query);

    // Then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getShopId()).isEqualTo(1L);
    assertThat(result.getContent().get(0).getPrimaryItemInfo().getName()).isEqualTo("Primary A");

    then(couponQueryRepository).should(times(1)).searchCoupons(eq(query), any(Pageable.class));
  }

  @Test
  @DisplayName("쿠폰 검색 성공 - 상품별 조회")
  void searchCoupons_상품별조회_성공() {
    // Given
    CouponSearchQuery query =
        CouponSearchQuery.builder()
            .primaryItemId(100L)
            .page(0)
            .size(10)
            .sortBy("createdAt")
            .sortDirection("desc")
            .build();

    Pageable pageable = PageRequest.of(0, 10);
    List<CouponDto> itemCouponDtoList =
        Arrays.asList(
            createCouponDto(
                1L,
                "CP1",
                "Title1",
                1L,
                10,
                LocalDate.now().plusDays(10),
                true,
                LocalDateTime.now(),
                101L,
                "Designer A",
                201L,
                "Primary A",
                "Cat A",
                301L,
                "Secondary A"));
    Page<CouponDto> couponPage =
        new PageImpl<>(itemCouponDtoList, pageable, itemCouponDtoList.size());

    given(couponQueryRepository.searchCoupons(eq(query), any(Pageable.class)))
        .willReturn(couponPage);

    // When
    PagedResult<CouponResponse> result = couponQueryService.searchCoupons(query);

    // Then
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getPrimaryItemInfo().getItemId()).isEqualTo(201L);
    assertThat(result.getContent().get(0).getPrimaryItemInfo().getName()).isEqualTo("Primary A");

    then(couponQueryRepository).should(times(1)).searchCoupons(eq(query), any(Pageable.class));
  }

  @Test
  @DisplayName("쿠폰 검색 성공 - 빈 결과")
  void searchCoupons_빈결과_성공() {
    // Given
    CouponSearchQuery query =
        CouponSearchQuery.builder()
            .couponCode("NONEXISTENT")
            .page(0)
            .size(10)
            .sortBy("createdAt")
            .sortDirection("desc")
            .build();

    Pageable pageable = PageRequest.of(0, 10);
    Page<CouponDto> emptyPage = new PageImpl<>(Arrays.asList(), pageable, 0);

    given(couponQueryRepository.searchCoupons(eq(query), any(Pageable.class)))
        .willReturn(emptyPage);

    // When
    PagedResult<CouponResponse> result = couponQueryService.searchCoupons(query);

    // Then
    assertThat(result.getContent()).isEmpty();
    assertThat(result.getPagination().getTotalItems()).isEqualTo(0);

    then(couponQueryRepository).should(times(1)).searchCoupons(eq(query), any(Pageable.class));
  }
}
