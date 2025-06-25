package com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate;

import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "primary_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PrimaryItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "primary_item_id")
  private Long primaryItemId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "shop_id", nullable = false)
  private Shop shopId;

  @Column(name = "primary_item_name", length = 100, nullable = false)
  private String primaryItemName;

  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false)
  private Category category;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
