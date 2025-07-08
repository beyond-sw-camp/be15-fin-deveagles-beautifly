package com.deveagles.be15_deveagles_be.features.shops.command.repository;

import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopRepository extends JpaRepository<Shop, Long> {
  Optional<Shop> findByBusinessNumber(String s);

  Optional<Shop> findByShopId(Long shopId);
}
