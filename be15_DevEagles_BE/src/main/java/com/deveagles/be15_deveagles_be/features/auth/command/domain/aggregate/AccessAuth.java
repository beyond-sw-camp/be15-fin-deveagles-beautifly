package com.deveagles.be15_deveagles_be.features.auth.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Entity
@Table(name = "access_auth")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccessAuth {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "auth_id", nullable = false)
  private Long authId;

  @Column(name = "access_id", nullable = false)
  private Long accessId;

  @Column(name = "staff_id", nullable = false)
  private Long staffId;

  @Column(name = "can_read", nullable = false)
  private boolean canRead = false;

  @Column(name = "can_write", nullable = false)
  private boolean canWrite = false;

  @Column(name = "can_delete", nullable = false)
  private boolean canDelete = false;

  @Column(name = "is_active", nullable = false)
  private boolean isActive = false;

  @Builder
  public AccessAuth(
      Long accessId,
      Long staffId,
      boolean canRead,
      boolean canWrite,
      boolean canDelete,
      boolean isActive) {
    this.accessId = accessId;
    this.staffId = staffId;
    this.canRead = canRead;
    this.canWrite = canWrite;
    this.canDelete = canDelete;
    this.isActive = isActive;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }
}
