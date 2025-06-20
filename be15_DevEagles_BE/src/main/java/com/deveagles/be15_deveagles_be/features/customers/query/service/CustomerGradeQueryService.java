package com.deveagles.be15_deveagles_be.features.customers.query.service;

import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.response.CustomerGradeResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerGradeQueryService {

  CustomerGradeResponse getCustomerGrade(Long gradeId);

  List<CustomerGradeResponse> getAllCustomerGrades();

  Page<CustomerGradeResponse> getCustomerGrades(Pageable pageable);
}
