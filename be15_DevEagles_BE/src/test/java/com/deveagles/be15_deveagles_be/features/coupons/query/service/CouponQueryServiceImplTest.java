package com.deveagles.be15_deveagles_be.features.coupons.query.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.deveagles.be15_deveagles_be.common.dto.PagedResult;
import com.deveagles.be15_deveagles_be.features.coupons.entity.Coupon;
import com.deveagles.be15_deveagles_be.features.coupons.query.dto.CouponQuery;
import com.deveagles.be15_deveagles_be.features.coupons.query.dto.CouponResponse;
import com.deveagles.be15_deveagles_be.features.coupons.repository.CouponRepository;
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
class CouponQueryServiceImplTest {

  @Mock private CouponRepository couponRepository;

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

  @Test
  @DisplayName("쿠폰 ID로 조회 성공")
  void getCouponById_성공() {
    // Given
    given(couponRepository.findById(1L)).willReturn(Optional.of(activeCoupon));

    // When
    Optional<CouponResponse> result = couponQueryService.getCouponById(1L);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getId()).isEqualTo(1L);
    assertThat(result.get().getCouponCode()).isEqualTo("CP241201ABCD1234");
    assertThat(result.get().getCouponTitle()).isEqualTo("활성 쿠폰");

    then(couponRepository).should(times(1)).findById(1L);
  }

  @Test
  @DisplayName("쿠폰 ID로 조회 실패 - 존재하지 않는 쿠폰")
  void getCouponById_존재하지않는쿠폰_실패() {
    // Given
    given(couponRepository.findById(999L)).willReturn(Optional.empty());

    // When
    Optional<CouponResponse> result = couponQueryService.getCouponById(999L);

    // Then
    assertThat(result).isEmpty();

    then(couponRepository).should(times(1)).findById(999L);
  }

  @Test
  @DisplayName("쿠폰 ID로 조회 실패 - 삭제된 쿠폰")
  void getCouponById_삭제된쿠폰_실패() {
    // Given
    given(couponRepository.findById(3L)).willReturn(Optional.of(deletedCoupon));

    // When
    Optional<CouponResponse> result = couponQueryService.getCouponById(3L);

    // Then
    assertThat(result).isEmpty();

    then(couponRepository).should(times(1)).findById(3L);
  }

  @Test
  @DisplayName("쿠폰 코드로 조회 성공")
  void getCouponByCode_성공() {
    // Given
    String couponCode = "CP241201ABCD1234";
    given(couponRepository.findByCouponCodeAndDeletedAtIsNull(couponCode))
        .willReturn(Optional.of(activeCoupon));

    // When
    Optional<CouponResponse> result = couponQueryService.getCouponByCode(couponCode);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().getCouponCode()).isEqualTo(couponCode);

    then(couponRepository).should(times(1)).findByCouponCodeAndDeletedAtIsNull(couponCode);
  }

  @Test
  @DisplayName("쿠폰 코드로 조회 실패 - 존재하지 않는 코드")
  void getCouponByCode_존재하지않는코드_실패() {
    // Given
    String nonExistentCode = "NONEXISTENT";
    given(couponRepository.findByCouponCodeAndDeletedAtIsNull(nonExistentCode))
        .willReturn(Optional.empty());

    // When
    Optional<CouponResponse> result = couponQueryService.getCouponByCode(nonExistentCode);

    // Then
    assertThat(result).isEmpty();

    then(couponRepository).should(times(1)).findByCouponCodeAndDeletedAtIsNull(nonExistentCode);
  }

  @Test
  @DisplayName("쿠폰 검색 성공 - 전체 조회")
  void searchCoupons_전체조회_성공() {
    // Given
    CouponQuery query =
        CouponQuery.builder().page(0).size(10).sortBy("createdAt").sortDirection("desc").build();

    Pageable pageable = PageRequest.of(0, 10);
    Page<Coupon> couponPage = new PageImpl<>(couponList, pageable, couponList.size());

    given(couponRepository.searchCoupons(eq(query), any(Pageable.class))).willReturn(couponPage);

    // When
    PagedResult<CouponResponse> result = couponQueryService.searchCoupons(query);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(2);
    assertThat(result.getPagination().getTotalItems()).isEqualTo(2);
    assertThat(result.getPagination().getTotalPages()).isEqualTo(1);
    assertThat(result.getPagination().getCurrentPage()).isEqualTo(0);

    then(couponRepository).should(times(1)).searchCoupons(eq(query), any(Pageable.class));
  }

  @Test
  @DisplayName("쿠폰 검색 성공 - 활성 쿠폰만")
  void searchCoupons_활성쿠폰만_성공() {
    // Given
    CouponQuery query =
        CouponQuery.builder()
            .isActive(true)
            .page(0)
            .size(10)
            .sortBy("createdAt")
            .sortDirection("desc")
            .build();

    Pageable pageable = PageRequest.of(0, 10);
    Page<Coupon> couponPage = new PageImpl<>(Arrays.asList(activeCoupon), pageable, 1);

    given(couponRepository.searchCoupons(eq(query), any(Pageable.class))).willReturn(couponPage);

    // When
    PagedResult<CouponResponse> result = couponQueryService.searchCoupons(query);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getIsActive()).isTrue();

    then(couponRepository).should(times(1)).searchCoupons(eq(query), any(Pageable.class));
  }

  @Test
  @DisplayName("쿠폰 검색 성공 - 매장별 조회")
  void searchCoupons_매장별조회_성공() {
    // Given
    CouponQuery query =
        CouponQuery.builder()
            .shopId(1L)
            .page(0)
            .size(10)
            .sortBy("createdAt")
            .sortDirection("desc")
            .build();

    Pageable pageable = PageRequest.of(0, 10);
    Page<Coupon> couponPage = new PageImpl<>(Arrays.asList(activeCoupon), pageable, 1);

    given(couponRepository.searchCoupons(eq(query), any(Pageable.class))).willReturn(couponPage);

    // When
    PagedResult<CouponResponse> result = couponQueryService.searchCoupons(query);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getShopId()).isEqualTo(1L);

    then(couponRepository).should(times(1)).searchCoupons(eq(query), any(Pageable.class));
  }

  @Test
  @DisplayName("쿠폰 검색 성공 - 상품별 조회")
  void searchCoupons_상품별조회_성공() {
    // Given
    CouponQuery query =
        CouponQuery.builder()
            .primaryItemId(100L)
            .page(0)
            .size(10)
            .sortBy("createdAt")
            .sortDirection("desc")
            .build();

    Pageable pageable = PageRequest.of(0, 10);
    Page<Coupon> couponPage = new PageImpl<>(Arrays.asList(activeCoupon), pageable, 1);

    given(couponRepository.searchCoupons(eq(query), any(Pageable.class))).willReturn(couponPage);

    // When
    PagedResult<CouponResponse> result = couponQueryService.searchCoupons(query);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).hasSize(1);
    assertThat(result.getContent().get(0).getPrimaryItemId()).isEqualTo(100L);

    then(couponRepository).should(times(1)).searchCoupons(eq(query), any(Pageable.class));
  }

  @Test
  @DisplayName("쿠폰 검색 성공 - 빈 결과")
  void searchCoupons_빈결과_성공() {
    // Given
    CouponQuery query =
        CouponQuery.builder()
            .couponCode("NONEXISTENT")
            .page(0)
            .size(10)
            .sortBy("createdAt")
            .sortDirection("desc")
            .build();

    Pageable pageable = PageRequest.of(0, 10);
    Page<Coupon> couponPage = new PageImpl<>(Arrays.asList(), pageable, 0);

    given(couponRepository.searchCoupons(eq(query), any(Pageable.class))).willReturn(couponPage);

    // When
    PagedResult<CouponResponse> result = couponQueryService.searchCoupons(query);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEmpty();
    assertThat(result.getPagination().getTotalItems()).isEqualTo(0);

    then(couponRepository).should(times(1)).searchCoupons(eq(query), any(Pageable.class));
  }
}
