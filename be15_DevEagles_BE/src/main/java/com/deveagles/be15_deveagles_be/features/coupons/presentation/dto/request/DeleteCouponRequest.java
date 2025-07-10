package com.deveagles.be15_deveagles_be.features.coupons.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteCouponRequest {

  @NotNull(message = "쿠폰 ID는 필수입니다") private Long id;

  @NotNull(message = "매장 ID는 필수입니다") private Long shopId;
}
