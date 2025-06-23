package com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "industry")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Industry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "industry_id", nullable = false)
  private Long industryId;

  @Column(name = "industry_name", nullable = false)
  private String industryName;
}
