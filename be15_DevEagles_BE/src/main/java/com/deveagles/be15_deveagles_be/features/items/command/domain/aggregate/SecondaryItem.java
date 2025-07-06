package com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "secondary_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SecondaryItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "secondary_item_id")
  private Long secondaryItemId;

  @Column(name = "primary_item_id", nullable = false)
  private Long primaryItemId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "primary_item_id", insertable = false, updatable = false)
  private PrimaryItem primaryItem;

  @Column(name = "secondary_item_name")
  private String secondaryItemName;

  @Column(name = "secondary_item_price")
  private Integer secondaryItemPrice;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive;

  @Column(name = "time_taken")
  private Integer timeTaken;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "modified_at", nullable = false)
  private LocalDateTime modifiedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
    this.modifiedAt = LocalDateTime.now();
  }

  public void updateSecondaryItem(
      String secondaryItemName, Integer secondaryItemPrice, Integer timeTaken, boolean isActive) {
    this.secondaryItemName = secondaryItemName;
    this.secondaryItemPrice = secondaryItemPrice;
    this.timeTaken = timeTaken;
    this.isActive = isActive;
  }

  public void setModifiedAt() {
    this.modifiedAt = LocalDateTime.now();
  }

  public void setDeletedAt() {
    this.deletedAt = LocalDateTime.now();
  }
}
