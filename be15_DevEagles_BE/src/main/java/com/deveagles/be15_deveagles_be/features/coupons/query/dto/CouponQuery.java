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

  private Long id;
  private String couponCode;
  private String couponTitle;
  private Long staffId;
  private Long primaryItemId;
  private Long secondaryItemId;
  private Integer discountRate;
  private LocalDate expirationDate;
  private Boolean isActive;
  private Integer page;
  private Integer size;
  private String sortBy;
  private String sortDirection;
}
