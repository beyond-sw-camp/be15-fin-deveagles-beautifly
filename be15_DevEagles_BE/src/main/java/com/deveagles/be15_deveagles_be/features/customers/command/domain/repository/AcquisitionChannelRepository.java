package com.deveagles.be15_deveagles_be.features.customers.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.AcquisitionChannel;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcquisitionChannelRepository extends JpaRepository<AcquisitionChannel, Long> {

  Optional<AcquisitionChannel> findByChannelName(String channelName);

  boolean existsByChannelName(String channelName);
}
