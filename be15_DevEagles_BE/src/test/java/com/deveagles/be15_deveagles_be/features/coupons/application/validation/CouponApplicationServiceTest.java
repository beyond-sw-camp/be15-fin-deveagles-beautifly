package com.deveagles.be15_deveagles_be.features.coupons.application.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.features.coupons.common.CouponResponseFactory;
import com.deveagles.be15_deveagles_be.features.coupons.domain.entity.Coupon;
import com.deveagles.be15_deveagles_be.features.coupons.domain.vo.DiscountResult;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.request.CouponApplicationRequest;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response.CouponApplicationResponse;
import com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.response.CouponValidationResponse;
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
  @Mock private CouponResponseFactory couponResponseFactory;

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
    CouponValidationResponse expectedResult = new CouponValidationResponse(true, null, validCoupon);
    given(couponValidationService.validateForSale(request)).willReturn(expectedResult);

    // When
    CouponValidationResponse result = couponApplicationService.validateCoupon(request);

    // Then
    assertThat(result).isEqualTo(expectedResult);
    then(couponValidationService).should(times(1)).validateForSale(request);
  }

  @Test
  @DisplayName("쿠폰 적용 성공")
  void applyCoupon_성공() {
    // Given
    CouponValidationResponse validationResult =
        new CouponValidationResponse(true, null, validCoupon);

    CouponApplicationResponse expectedResponse =
        new CouponApplicationResponse(true, null, validCoupon, 10, 1000, 9000);

    given(couponValidationService.validateForSale(request)).willReturn(validationResult);
    // 실제 계산 결과를 받아서 응답 생성하므로, any()를 사용
    given(
            couponResponseFactory.createSuccessResponse(
                org.mockito.ArgumentMatchers.eq(validCoupon),
                org.mockito.ArgumentMatchers.any(DiscountResult.class)))
        .willReturn(expectedResponse);

    // When
    CouponApplicationResponse result = couponApplicationService.applyCoupon(request);

    // Then
    assertThat(result.isSuccess()).isTrue();
    assertThat(result.getDiscountRate()).isEqualTo(10);
    assertThat(result.getDiscountAmount()).isEqualTo(1000);
    assertThat(result.getFinalAmount()).isEqualTo(9000);
    then(couponValidationService).should(times(1)).validateForSale(request);
    then(couponResponseFactory)
        .should(times(1))
        .createSuccessResponse(
            org.mockito.ArgumentMatchers.eq(validCoupon),
            org.mockito.ArgumentMatchers.any(DiscountResult.class));
  }

  @Test
  @DisplayName("쿠폰 적용 실패 - 검증 실패")
  void applyCoupon_검증실패() {
    // Given
    CouponValidationResponse validationResult =
        new CouponValidationResponse(false, "쿠폰을 찾을 수 없습니다", null);
    CouponApplicationResponse expectedFailedResponse =
        new CouponApplicationResponse(false, "쿠폰을 찾을 수 없습니다", null, null, null, null);

    given(couponValidationService.validateForSale(request)).willReturn(validationResult);
    given(couponResponseFactory.createFailedResponse("쿠폰을 찾을 수 없습니다"))
        .willReturn(expectedFailedResponse);

    // When
    CouponApplicationResponse result = couponApplicationService.applyCoupon(request);

    // Then
    assertThat(result.isSuccess()).isFalse();
    assertThat(result.getErrorMessage()).isEqualTo("쿠폰을 찾을 수 없습니다");
    then(couponValidationService).should(times(1)).validateForSale(request);
    then(couponResponseFactory).should(times(1)).createFailedResponse("쿠폰을 찾을 수 없습니다");
  }

  @Test
  @DisplayName("쿠폰 적용 실패 - 유효하지 않은 금액")
  void applyCoupon_유효하지않은금액() {
    // Given
    CouponApplicationRequest invalidRequest =
        CouponApplicationRequest.builder()
            .couponCode("CP241201ABCD1234")
            .shopId(1L)
            .staffId(1L)
            .primaryItemId(100L)
            .secondaryItemId(200L)
            .originalAmount(0) // 유효하지 않은 금액
            .build();

    CouponValidationResponse validationResult =
        new CouponValidationResponse(true, null, validCoupon);

    given(couponValidationService.validateForSale(invalidRequest)).willReturn(validationResult);

    // When & Then - 이제 BusinessException이 발생해야 함
    assertThatThrownBy(() -> couponApplicationService.applyCoupon(invalidRequest))
        .isInstanceOf(BusinessException.class)
        .extracting(ex -> ((BusinessException) ex).getOriginalMessage())
        .isEqualTo("유효하지 않은 금액입니다");

    then(couponValidationService).should(times(1)).validateForSale(invalidRequest);
  }
}
