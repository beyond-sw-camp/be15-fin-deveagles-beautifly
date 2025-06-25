package com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "item_sales")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ItemSales {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "item_sales_id")
  private Long itemSalesId;

  @Column(name = "secondary_item_id", nullable = false)
  private Long secondaryItemId;

  @Column(name = "sales_id", nullable = false)
  private Long salesId;

  @Column(name = "quantity")
  private Integer quantity;

  @Column(name = "discount_rate")
  private Integer discountRate;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "modified_at", nullable = false)
  private LocalDateTime modifiedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @Column(name = "coupon_id", nullable = false)
  private Long couponId;
}
