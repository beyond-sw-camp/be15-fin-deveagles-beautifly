package com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_grade")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CustomerGrade {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "customer_grade_id")
  private Long id;

  @Column(name = "customer_grade_name", nullable = false, length = 10)
  private String customerGradeName;

  @Column(name = "discount_rate", nullable = false)
  private Integer discountRate;

  // Business methods
  public void updateGradeName(String customerGradeName) {
    this.customerGradeName = customerGradeName;
  }

  public void updateDiscountRate(Integer discountRate) {
    this.discountRate = discountRate;
  }
}
