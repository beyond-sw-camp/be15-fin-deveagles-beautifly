package com.deveagles.be15_deveagles_be.features.customers.command.application.service;

import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.CreateCustomerGradeRequest;
import com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request.UpdateCustomerGradeRequest;

public interface CustomerGradeCommandService {

  Long createCustomerGrade(CreateCustomerGradeRequest request);

  void updateCustomerGrade(Long gradeId, UpdateCustomerGradeRequest request);

  void deleteCustomerGrade(Long gradeId);
}
