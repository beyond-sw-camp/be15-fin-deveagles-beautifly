package com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request;

import java.time.LocalDate;

import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Customer;

public record UpdateCustomerRequest(
    Long customerId,
    String customerName,
    String phoneNumber,
    String memo,
    Customer.Gender gender,
    Long channelId,
    Long staffId,
    Long customerGradeId,
    LocalDate birthdate,
    Boolean marketingConsent,
    Boolean notificationConsent
) {}
