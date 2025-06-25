package com.deveagles.be15_deveagles_be.features.workflows.execution.application.service;

public interface TriggerCheckService {

  void onCustomerVisit(CustomerVisitEvent event);

  void onCustomerRegistration(CustomerRegistrationEvent event);

  void onPaymentCompleted(PaymentCompletedEvent event);

  class CustomerVisitEvent {
    private final Long customerId;
    private final Long shopId;
    private final String treatmentId;
    private final java.time.LocalDateTime visitTime;

    public CustomerVisitEvent(
        Long customerId, Long shopId, String treatmentId, java.time.LocalDateTime visitTime) {
      this.customerId = customerId;
      this.shopId = shopId;
      this.treatmentId = treatmentId;
      this.visitTime = visitTime;
    }

    public Long getCustomerId() {
      return customerId;
    }

    public Long getShopId() {
      return shopId;
    }

    public String getTreatmentId() {
      return treatmentId;
    }

    public java.time.LocalDateTime getVisitTime() {
      return visitTime;
    }
  }

  class CustomerRegistrationEvent {
    private final Long customerId;
    private final Long shopId;
    private final java.time.LocalDateTime registrationTime;

    public CustomerRegistrationEvent(
        Long customerId, Long shopId, java.time.LocalDateTime registrationTime) {
      this.customerId = customerId;
      this.shopId = shopId;
      this.registrationTime = registrationTime;
    }

    public Long getCustomerId() {
      return customerId;
    }

    public Long getShopId() {
      return shopId;
    }

    public java.time.LocalDateTime getRegistrationTime() {
      return registrationTime;
    }
  }

  class PaymentCompletedEvent {
    private final Long customerId;
    private final Long shopId;
    private final Long amount;
    private final java.time.LocalDateTime paymentTime;

    public PaymentCompletedEvent(
        Long customerId, Long shopId, Long amount, java.time.LocalDateTime paymentTime) {
      this.customerId = customerId;
      this.shopId = shopId;
      this.amount = amount;
      this.paymentTime = paymentTime;
    }

    public Long getCustomerId() {
      return customerId;
    }

    public Long getShopId() {
      return shopId;
    }

    public Long getAmount() {
      return amount;
    }

    public java.time.LocalDateTime getPaymentTime() {
      return paymentTime;
    }
  }
}
