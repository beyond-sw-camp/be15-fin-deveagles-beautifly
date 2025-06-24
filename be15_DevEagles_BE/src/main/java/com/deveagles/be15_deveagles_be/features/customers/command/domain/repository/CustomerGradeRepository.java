package com.deveagles.be15_deveagles_be.features.customers.command.domain.repository;

import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.CustomerGrade;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerGradeRepository extends JpaRepository<CustomerGrade, Long> {

  Optional<CustomerGrade> findByCustomerGradeName(String customerGradeName);

  Optional<CustomerGrade> findByCustomerGradeNameAndShopId(String customerGradeName, Long shopId);

  Optional<CustomerGrade> findByIdAndShopId(Long gradeId, Long shopId);

  List<CustomerGrade> findByShopId(Long shopId);

  boolean existsByCustomerGradeName(String customerGradeName);

  boolean existsByCustomerGradeNameAndShopId(String customerGradeName, Long shopId);

  boolean existsByIdAndShopId(Long gradeId, Long shopId);
}
