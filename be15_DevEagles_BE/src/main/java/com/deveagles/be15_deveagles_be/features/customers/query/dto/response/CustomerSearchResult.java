package com.deveagles.be15_deveagles_be.features.customers.query.dto.response;

import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Customer;

public record CustomerSearchResult(
    Long customerId,
    String customerName,
    String phoneNumber,
    Long customerGradeId,
    String customerGradeName, // 조인해서 가져올 예정
    Customer.Gender gender) {

  public static CustomerSearchResult from(CustomerDocument document) {
    return new CustomerSearchResult(
        document.getCustomerId(),
        document.getCustomerName(),
        document.getPhoneNumber(),
        document.getCustomerGradeId(),
        null, // 필요시 추후 조인으로 처리
        document.getGender() != null ? Customer.Gender.valueOf(document.getGender()) : null);
  }

  public static CustomerSearchResult from(Customer customer) {
    return new CustomerSearchResult(
        customer.getId(),
        customer.getCustomerName(),
        customer.getPhoneNumber(),
        customer.getCustomerGradeId(),
        null, // 필요시 추후 조인으로 처리
        customer.getGender());
  }
}
