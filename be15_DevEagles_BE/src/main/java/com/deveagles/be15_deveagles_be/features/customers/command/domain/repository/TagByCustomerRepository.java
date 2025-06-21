package com.deveagles.be15_deveagles_be.features.customers.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.TagByCustomer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagByCustomerRepository
    extends JpaRepository<TagByCustomer, TagByCustomer.TagByCustomerId> {

  List<TagByCustomer> findByCustomerId(Long customerId);

  void deleteByCustomerIdAndTagId(Long customerId, Long tagId);

  boolean existsByCustomerIdAndTagId(Long customerId, Long tagId);
}
