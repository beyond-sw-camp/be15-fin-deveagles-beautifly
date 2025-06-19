package com.deveagles.be15_deveagles_be.features.campaigns.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.campaigns.command.domain.aggregate.Campaign;
import java.util.Optional;

public interface CampaignRepository {
  Campaign save(Campaign campaign);

  Optional<Campaign> findById(Long id);

  void delete(Campaign campaign);
}
