package com.deveagles.be15_deveagles_be.features.shops.command.repository;

import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.SNS;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnsRepository extends JpaRepository<SNS, Long> {
  List<SNS> findByShopId(Long shopId);
}
