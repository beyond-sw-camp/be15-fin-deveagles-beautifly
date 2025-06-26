package com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate;

public enum Category {
  SERVICE,
  PRODUCT;

  public boolean isService() {
    return this == SERVICE;
  }
}
