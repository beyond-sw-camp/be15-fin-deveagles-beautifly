package com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "message_settings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MessageSettings {

  @Id
  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "sender_number", length = 11)
  private String senderNumber;

  @Column(name = "can_alimtalk", nullable = false)
  private boolean canAlimtalk;

  @Column(name = "point", nullable = false)
  private Long point;

  public void update(String senderNumber, Boolean canAlimtalk) {
    if (senderNumber != null) this.senderNumber = senderNumber;
    if (canAlimtalk != null) this.canAlimtalk = canAlimtalk;
  }

  public void addPoint(long added) {
    if (added <= 0) throw new IllegalArgumentException("포인트는 0보다 커야 합니다.");
    this.point += added;
  }

  public void usePoint(long used) {
    if (this.point < used) throw new IllegalStateException("포인트 부족");
    this.point -= used;
  }
}
