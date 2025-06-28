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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "message_settings_id")
  private Long id;

  @Column(name = "shop_id", nullable = false)
  private Long shopId;

  @Column(name = "sender_number", length = 11)
  private String senderNumber;

  @Column(name = "can_alimtalk", nullable = false)
  private boolean canAlimtalk;

  @Column(name = "point", nullable = false)
  private Long point;

  public void usePoint(long used) {
    if (this.point < used) {
      throw new IllegalStateException("문자 포인트가 부족합니다. 현재 잔여: " + this.point);
    }
    this.point -= used;
  }

  public void addPoint(long added) {
    if (added <= 0) {
      throw new IllegalArgumentException("추가할 포인트는 0보다 커야 합니다.");
    }
    this.point += added;
  }
}
