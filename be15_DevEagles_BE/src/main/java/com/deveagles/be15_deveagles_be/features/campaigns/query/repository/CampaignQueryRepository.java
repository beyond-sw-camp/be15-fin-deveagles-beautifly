package com.deveagles.be15_deveagles_be.features.campaigns.query.repository;

import com.deveagles.be15_deveagles_be.features.campaigns.command.domain.aggregate.Campaign;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampaignQueryRepository extends JpaRepository<Campaign, Long> {

  Page<Campaign> findByShopIdAndDeletedAtIsNullOrderByCreatedAtDesc(Long shopId, Pageable pageable);

  Optional<Campaign> findByIdAndDeletedAtIsNull(Long id);
}
