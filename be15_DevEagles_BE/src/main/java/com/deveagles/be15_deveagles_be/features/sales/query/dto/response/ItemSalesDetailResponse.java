package com.deveagles.be15_deveagles_be.features.sales.query.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemSalesDetailResponse {

  // sales
  private Long salesId;
  private String salesDate;
  private String customerName;
  private String staffName;
  private Integer retailPrice;
  private Integer salesDiscountRate;
  private Integer discountAmount;
  private Integer totalAmount;
  private String couponName;
  private String salesMemo;

  // item_sales
  private Long itemSalesId;
  private String secondaryItemName;
  private Integer itemDiscountRate;
  private Integer quantity;

  // payments
  private List<PaymentsDTO> payments;

  // customer_membership_history
  private Long customerPrepaidPassId;
  private Long customerSessionPassId;
  private Integer usedCount;
}
