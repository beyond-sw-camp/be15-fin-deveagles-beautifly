package com.deveagles.be15_deveagles_be.features.auth.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Getter
@Entity
@Table(name = "access_list")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AccessList {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "access_id", nullable = false)
  private Long accessId;

  @Column(name = "access_name", nullable = false)
  private String accessName;
}
