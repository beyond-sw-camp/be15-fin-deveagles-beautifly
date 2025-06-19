package com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponApplicationRequest {

  @NotBlank(message = "쿠폰 코드는 필수입니다")
  private String couponCode;

  @NotNull(message = "매장 ID는 필수입니다") private Long shopId;

  private Long staffId;

  @NotNull(message = "1차 상품 ID는 필수입니다") private Long primaryItemId;

  private Long secondaryItemId;

  @Positive(message = "원금액은 0보다 커야 합니다") private Integer originalAmount;
}
