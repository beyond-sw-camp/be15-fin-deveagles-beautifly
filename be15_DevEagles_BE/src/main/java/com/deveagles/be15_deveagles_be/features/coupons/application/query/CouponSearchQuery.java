package com.deveagles.be15_deveagles_be.features.coupons.application.query;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponSearchQuery {

  private String couponCode;
  private String couponTitle;
  private Long shopId;
  private Long staffId;
  private Long primaryItemId;
  private Boolean isActive;
  private LocalDate expirationDateFrom;
  private LocalDate expirationDateTo;

  private Integer page;
  private Integer size;
  private String sortBy;
  private String sortDirection;
}
