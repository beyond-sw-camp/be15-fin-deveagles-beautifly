package com.deveagles.be15_deveagles_be.features.coupons.application.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.deveagles.be15_deveagles_be.features.coupons.common.CouponResponseFactory;
import com.deveagles.be15_deveagles_be.features.coupons.domain.entity.Coupon;
import com.deveagles.be15_deveagles_be.features.coupons.infrastructure.repository.CouponJpaRepository;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.request.CouponApplicationRequest;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response.CouponValidationResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponValidationService 단위 테스트")
class CouponValidationServiceTest {

  @Mock private CouponJpaRepository couponJpaRepository;
  @Mock private CouponResponseFactory couponResponseFactory;

  @InjectMocks private CouponValidationService couponValidationService;

  private Coupon validCoupon;
  private CouponApplicationRequest request;

  @BeforeEach
  void setUp() {
    validCoupon =
        Coupon.builder()
            .id(1L)
            .couponCode("CP241201ABCD1234")
            .couponTitle("테스트 쿠폰")
            .shopId(1L)
            .staffId(1L)
            .primaryItemId(100L)
            .secondaryItemId(200L)
            .discountRate(10)
            .expirationDate(LocalDate.now().plusDays(30))
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .build();

    request =
        CouponApplicationRequest.builder()
            .couponCode("CP241201ABCD1234")
            .shopId(1L)
            .staffId(1L)
            .primaryItemId(100L)
            .secondaryItemId(200L)
            .originalAmount(10000)
            .build();
  }

  @Test
  @DisplayName("쿠폰 검증 성공")
  void validateForSale_성공() {
    // Given
    CouponValidationResponse expectedResponse =
        new CouponValidationResponse(true, null, validCoupon);
    given(couponJpaRepository.findByCouponCodeAndDeletedAtIsNull("CP241201ABCD1234"))
        .willReturn(Optional.of(validCoupon));
    given(couponResponseFactory.createValidResponse(validCoupon)).willReturn(expectedResponse);

    // When
    CouponValidationResponse result = couponValidationService.validateForSale(request);

    // Then
    assertThat(result.isValid()).isTrue();
    assertThat(result.getCoupon()).isEqualTo(validCoupon);
    assertThat(result.getErrorMessage()).isNull();
  }

  @Test
  @DisplayName("쿠폰 검증 실패 - 쿠폰 없음")
  void validateForSale_쿠폰없음_실패() {
    // Given
    CouponValidationResponse expectedResponse =
        new CouponValidationResponse(false, "쿠폰을 찾을 수 없습니다", null);
    given(couponJpaRepository.findByCouponCodeAndDeletedAtIsNull("CP241201ABCD1234"))
        .willReturn(Optional.empty());
    given(couponResponseFactory.createInvalidResponse("쿠폰을 찾을 수 없습니다"))
        .willReturn(expectedResponse);

    // When
    CouponValidationResponse result = couponValidationService.validateForSale(request);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).isEqualTo("쿠폰을 찾을 수 없습니다");
  }

  @Test
  @DisplayName("쿠폰 검증 실패 - 다른 매장")
  void validateForSale_다른매장_실패() {
    // Given
    CouponApplicationRequest wrongShopRequest =
        CouponApplicationRequest.builder()
            .couponCode("CP241201ABCD1234")
            .shopId(999L)
            .staffId(1L)
            .primaryItemId(100L)
            .secondaryItemId(200L)
            .originalAmount(10000)
            .build();

    CouponValidationResponse expectedResponse =
        new CouponValidationResponse(false, "해당 매장에서 사용할 수 없는 쿠폰입니다", null);
    given(couponJpaRepository.findByCouponCodeAndDeletedAtIsNull("CP241201ABCD1234"))
        .willReturn(Optional.of(validCoupon));
    given(couponResponseFactory.createInvalidResponse("해당 매장에서 사용할 수 없는 쿠폰입니다"))
        .willReturn(expectedResponse);

    // When
    CouponValidationResponse result = couponValidationService.validateForSale(wrongShopRequest);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).isEqualTo("해당 매장에서 사용할 수 없는 쿠폰입니다");
  }

  @Test
  @DisplayName("쿠폰 검증 성공 - staff 전체 적용 가능")
  void validateForSale_직원전체적용_성공() {
    // Given
    Coupon allStaffCoupon =
        Coupon.builder()
            .id(1L)
            .couponCode("CP241201ABCD1234")
            .couponTitle("전체 직원 쿠폰")
            .shopId(1L)
            .staffId(null)
            .primaryItemId(100L)
            .secondaryItemId(200L)
            .discountRate(10)
            .expirationDate(LocalDate.now().plusDays(30))
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .build();

    CouponApplicationRequest anyStaffRequest =
        CouponApplicationRequest.builder()
            .couponCode("CP241201ABCD1234")
            .shopId(1L)
            .staffId(999L)
            .primaryItemId(100L)
            .secondaryItemId(200L)
            .originalAmount(10000)
            .build();

    CouponValidationResponse expectedResponse =
        new CouponValidationResponse(true, null, allStaffCoupon);
    given(couponJpaRepository.findByCouponCodeAndDeletedAtIsNull("CP241201ABCD1234"))
        .willReturn(Optional.of(allStaffCoupon));
    given(couponResponseFactory.createValidResponse(allStaffCoupon)).willReturn(expectedResponse);

    // When
    CouponValidationResponse result = couponValidationService.validateForSale(anyStaffRequest);

    // Then
    assertThat(result.isValid()).isTrue();
  }

  @Test
  @DisplayName("쿠폰 검증 성공 - 1차 상품 전체 적용 가능")
  void validateForSale_1차상품전체적용_성공() {
    // Given
    Coupon primaryItemOnlyCoupon =
        Coupon.builder()
            .id(1L)
            .couponCode("CP241201ABCD1234")
            .couponTitle("1차 상품 전체 쿠폰")
            .shopId(1L)
            .staffId(1L)
            .primaryItemId(100L)
            .secondaryItemId(null)
            .discountRate(10)
            .expirationDate(LocalDate.now().plusDays(30))
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .build();

    CouponApplicationRequest anySecondaryRequest =
        CouponApplicationRequest.builder()
            .couponCode("CP241201ABCD1234")
            .shopId(1L)
            .staffId(1L)
            .primaryItemId(100L)
            .secondaryItemId(999L)
            .originalAmount(10000)
            .build();

    CouponValidationResponse expectedResponse =
        new CouponValidationResponse(true, null, primaryItemOnlyCoupon);
    given(couponJpaRepository.findByCouponCodeAndDeletedAtIsNull("CP241201ABCD1234"))
        .willReturn(Optional.of(primaryItemOnlyCoupon));
    given(couponResponseFactory.createValidResponse(primaryItemOnlyCoupon))
        .willReturn(expectedResponse);

    // When
    CouponValidationResponse result = couponValidationService.validateForSale(anySecondaryRequest);

    // Then
    assertThat(result.isValid()).isTrue();
  }

  @Test
  @DisplayName("쿠폰 검증 실패 - 다른 1차 상품")
  void validateForSale_다른1차상품_실패() {
    // Given
    CouponApplicationRequest wrongPrimaryRequest =
        CouponApplicationRequest.builder()
            .couponCode("CP241201ABCD1234")
            .shopId(1L)
            .staffId(1L)
            .primaryItemId(999L)
            .secondaryItemId(200L)
            .originalAmount(10000)
            .build();

    CouponValidationResponse expectedResponse =
        new CouponValidationResponse(false, "해당 상품에 적용할 수 없는 쿠폰입니다", null);
    given(couponJpaRepository.findByCouponCodeAndDeletedAtIsNull("CP241201ABCD1234"))
        .willReturn(Optional.of(validCoupon));
    given(couponResponseFactory.createInvalidResponse("해당 상품에 적용할 수 없는 쿠폰입니다"))
        .willReturn(expectedResponse);

    // When
    CouponValidationResponse result = couponValidationService.validateForSale(wrongPrimaryRequest);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).isEqualTo("해당 상품에 적용할 수 없는 쿠폰입니다");
  }

  @Test
  @DisplayName("쿠폰 검증 실패 - 만료된 쿠폰")
  void validateForSale_만료된쿠폰_실패() {
    // Given
    Coupon expiredCoupon =
        Coupon.builder()
            .id(1L)
            .couponCode("CP241201ABCD1234")
            .couponTitle("만료된 쿠폰")
            .shopId(1L)
            .staffId(1L)
            .primaryItemId(100L)
            .secondaryItemId(200L)
            .discountRate(10)
            .expirationDate(LocalDate.now().minusDays(1))
            .isActive(true)
            .createdAt(LocalDateTime.now())
            .build();

    CouponValidationResponse expectedResponse =
        new CouponValidationResponse(false, "만료된 쿠폰입니다", null);
    given(couponJpaRepository.findByCouponCodeAndDeletedAtIsNull("CP241201ABCD1234"))
        .willReturn(Optional.of(expiredCoupon));
    given(couponResponseFactory.createInvalidResponse("만료된 쿠폰입니다")).willReturn(expectedResponse);

    // When
    CouponValidationResponse result = couponValidationService.validateForSale(request);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).isEqualTo("만료된 쿠폰입니다");
  }

  @Test
  @DisplayName("쿠폰 검증 실패 - 비활성 쿠폰")
  void validateForSale_비활성쿠폰_실패() {
    // Given
    Coupon inactiveCoupon =
        Coupon.builder()
            .id(1L)
            .couponCode("CP241201ABCD1234")
            .couponTitle("비활성 쿠폰")
            .shopId(1L)
            .staffId(1L)
            .primaryItemId(100L)
            .secondaryItemId(200L)
            .discountRate(10)
            .expirationDate(LocalDate.now().plusDays(30))
            .isActive(false)
            .createdAt(LocalDateTime.now())
            .build();

    CouponValidationResponse expectedResponse =
        new CouponValidationResponse(false, "비활성화된 쿠폰입니다", null);
    given(couponJpaRepository.findByCouponCodeAndDeletedAtIsNull("CP241201ABCD1234"))
        .willReturn(Optional.of(inactiveCoupon));
    given(couponResponseFactory.createInvalidResponse("비활성화된 쿠폰입니다")).willReturn(expectedResponse);

    // When
    CouponValidationResponse result = couponValidationService.validateForSale(request);

    // Then
    assertThat(result.isValid()).isFalse();
    assertThat(result.getErrorMessage()).isEqualTo("비활성화된 쿠폰입니다");
  }
}
