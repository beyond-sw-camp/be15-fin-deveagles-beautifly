package com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "message_click_link_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MessageClickLinkLog {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "click_id")
  private Long id;

  @Column(name = "message_link_id", nullable = false)
  private Long messageLinkId;

  @Column(name = "read_at", nullable = false)
  private LocalDateTime readAt;

  public static MessageClickLinkLog create(Long messageLinkId) {
    return MessageClickLinkLog.builder()
        .messageLinkId(messageLinkId)
        .readAt(LocalDateTime.now())
        .build();
  }

  public boolean isFrom(Long targetMessageLinkId) {
    return this.messageLinkId.equals(targetMessageLinkId);
  }
}
