package com.deveagles.be15_deveagles_be.features.campaigns.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.campaigns.command.domain.aggregate.Campaign;
import com.deveagles.be15_deveagles_be.features.campaigns.command.domain.repository.CampaignRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CampaignRepositoryImpl implements CampaignRepository {

  private final CampaignJpaRepository campaignJpaRepository;

  @Override
  public Campaign save(Campaign campaign) {
    return campaignJpaRepository.save(campaign);
  }

  @Override
  public Optional<Campaign> findById(Long id) {
    return campaignJpaRepository.findById(id);
  }

  @Override
  public void delete(Campaign campaign) {
    campaignJpaRepository.delete(campaign);
  }
}
