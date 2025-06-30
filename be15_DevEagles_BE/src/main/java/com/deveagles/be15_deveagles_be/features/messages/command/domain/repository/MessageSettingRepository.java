package com.deveagles.be15_deveagles_be.features.messages.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageSettings;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageSettingRepository extends JpaRepository<MessageSettings, Long> {
  Optional<MessageSettings> findByShopId(Long shopId);

  boolean existsByShopId(Long shopId);
}
