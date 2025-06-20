package com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "acquisition_channel")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AcquisitionChannel {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "channel_id")
  private Long id;

  @Column(name = "channel_name", nullable = false, length = 20)
  private String channelName;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "modified_at", nullable = false)
  private LocalDateTime modifiedAt;

  // Business methods
  public void updateChannelName(String channelName) {
    this.channelName = channelName;
  }
}
