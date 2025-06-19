package com.deveagles.be15_deveagles_be.features.customers.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Customer;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerJpaRepository extends JpaRepository<Customer, Long> {

  Optional<Customer> findByIdAndShopIdAndDeletedAtIsNull(Long id, Long shopId);

  List<Customer> findByShopIdAndDeletedAtIsNull(Long shopId);

  Optional<Customer> findByPhoneNumberAndShopIdAndDeletedAtIsNull(String phoneNumber, Long shopId);

  boolean existsByPhoneNumberAndShopIdAndDeletedAtIsNull(String phoneNumber, Long shopId);

  long countByShopIdAndDeletedAtIsNull(Long shopId);
}
