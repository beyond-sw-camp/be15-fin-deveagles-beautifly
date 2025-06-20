package com.deveagles.be15_deveagles_be.features.customers.command.application.dto.request;

import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Customer;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record CreateCustomerRequest(
    Long customerGradeId,
    Long shopId,
    Long staffId,
    String customerName,
    String phoneNumber,
    String memo,
    LocalDate birthdate,
    LocalDateTime registeredAt,
    Customer.Gender gender,
    Boolean marketingConsent,
    Boolean notificationConsent,
    Long channelId) {}
