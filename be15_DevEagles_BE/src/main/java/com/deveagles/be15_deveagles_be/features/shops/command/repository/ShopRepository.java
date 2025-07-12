package com.deveagles.be15_deveagles_be.features.shops.command.repository;

import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Shop;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShopRepository extends JpaRepository<Shop, Long> {
  Optional<Shop> findByBusinessNumber(String s);

  Optional<Shop> findByShopId(Long shopId);

  @Query("SELECT s.incentiveStatus FROM Shop s WHERE s.shopId = :shopId")
  Optional<Boolean> findIncentiveStatusByShopId(@Param("shopId") Long shopId);
}
