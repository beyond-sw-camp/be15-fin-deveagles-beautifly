package com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Tag {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tag_id")
  private Long id;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "tag_name", nullable = false, length = 10)
  private String tagName;

  @Column(name = "color_code", nullable = false, length = 20)
  private String colorCode;

  // Business methods
  public void updateTagInfo(String tagName, String colorCode) {
    this.tagName = tagName;
    this.colorCode = colorCode;
  }
}
