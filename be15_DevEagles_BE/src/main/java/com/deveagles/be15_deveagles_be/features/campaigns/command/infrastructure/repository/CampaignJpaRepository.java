package com.deveagles.be15_deveagles_be.features.campaigns.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.campaigns.command.domain.aggregate.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignJpaRepository extends JpaRepository<Campaign, Long> {}
