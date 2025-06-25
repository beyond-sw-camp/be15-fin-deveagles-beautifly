package com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "prepaid_pass_sales")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PrepaidPassSales {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "prepaid_pass_sales_id")
  private Long prepaidPassSalesId;

  @Column(name = "prepaid_pass_id", nullable = false)
  private Long prepaidPassId;

  @Column(name = "sales_id", nullable = false)
  private Long salesId;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "modified_at")
  private LocalDateTime modifiedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;
}
