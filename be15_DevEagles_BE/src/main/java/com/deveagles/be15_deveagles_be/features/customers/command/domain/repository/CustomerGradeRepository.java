package com.deveagles.be15_deveagles_be.features.customers.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.CustomerGrade;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerGradeRepository extends JpaRepository<CustomerGrade, Long> {

  Optional<CustomerGrade> findByCustomerGradeName(String customerGradeName);

  boolean existsByCustomerGradeName(String customerGradeName);
}
