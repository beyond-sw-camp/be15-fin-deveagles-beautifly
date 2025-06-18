package com.deveagles.be15_deveagles_be.features.coupons.query.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponQuery {

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
