package com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "message_click_link")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class MessageClickLink {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "message_link_id")
  private Long id;

  @Column(name = "short_token", nullable = false, length = 255)
  private String shortToken;

  @Column(name = "original_url", nullable = false, length = 255)
  private String originalUrl;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "message_id", nullable = false)
  private Long messageId;

  public boolean matchesToken(String token) {
    return this.shortToken.equals(token);
  }

  public boolean isForMessage(Long msgId) {
    return this.messageId.equals(msgId);
  }
}
