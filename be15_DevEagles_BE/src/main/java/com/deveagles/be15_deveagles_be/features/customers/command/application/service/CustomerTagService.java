package com.deveagles.be15_deveagles_be.features.customers.command.application.service;

public interface CustomerTagService {

  void addTagToCustomer(Long customerId, Long tagId, Long shopId);

  void removeTagFromCustomer(Long customerId, Long tagId, Long shopId);
}
