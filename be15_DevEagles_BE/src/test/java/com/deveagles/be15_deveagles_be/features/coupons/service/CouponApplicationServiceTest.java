package com.deveagles.be15_deveagles_be.features.coupons.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.deveagles.be15_deveagles_be.features.coupons.dto.CouponApplicationRequest;
import com.deveagles.be15_deveagles_be.features.coupons.dto.CouponApplicationResult;
import com.deveagles.be15_deveagles_be.features.coupons.dto.CouponValidationResult;
import com.deveagles.be15_deveagles_be.features.coupons.entity.Coupon;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("CouponApplicationService 단위 테스트")
class CouponApplicationServiceTest {

  @Mock private CouponValidationService couponValidationService;
  @Mock private CouponDiscountCalculator couponDiscountCalculator;

  @InjectMocks private CouponApplicationService couponApplicationService;

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
  void validateCoupon_성공() {
    // Given
    CouponValidationResult expectedResult = CouponValidationResult.valid(validCoupon);
    given(couponValidationService.validateForSale(request)).willReturn(expectedResult);

    // When
    CouponValidationResult result = couponApplicationService.validateCoupon(request);

    // Then
    assertThat(result).isEqualTo(expectedResult);
    then(couponValidationService).should(times(1)).validateForSale(request);
  }

  @Test
  @DisplayName("쿠폰 적용 성공")
  void applyCoupon_성공() {
    // Given
    CouponValidationResult validationResult = CouponValidationResult.valid(validCoupon);
    CouponApplicationResult expectedResult =
        CouponApplicationResult.success(validCoupon, 10, 1000, 9000);

    given(couponValidationService.validateForSale(request)).willReturn(validationResult);
    given(couponDiscountCalculator.calculateDiscount(validCoupon, 10000))
        .willReturn(expectedResult);

    // When
    CouponApplicationResult result = couponApplicationService.applyCoupon(request);

    // Then
    assertThat(result).isEqualTo(expectedResult);
    then(couponValidationService).should(times(1)).validateForSale(request);
    then(couponDiscountCalculator).should(times(1)).calculateDiscount(validCoupon, 10000);
  }

  @Test
  @DisplayName("쿠폰 적용 실패 - 검증 실패")
  void applyCoupon_검증실패() {
    // Given
    CouponValidationResult validationResult = CouponValidationResult.invalid("쿠폰을 찾을 수 없습니다");
    given(couponValidationService.validateForSale(request)).willReturn(validationResult);

    // When
    CouponApplicationResult result = couponApplicationService.applyCoupon(request);

    // Then
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getErrorMessage()).isEqualTo("쿠폰을 찾을 수 없습니다");
    then(couponValidationService).should(times(1)).validateForSale(request);
    then(couponDiscountCalculator).should(times(0)).calculateDiscount(validCoupon, 10000);
  }
}
