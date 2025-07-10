package com.deveagles.be15_deveagles_be.features.staffsales.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductIncentiveRates {

  private int service;
  private int product;
  private int sessionPass;
  private int prepaidPass;
}
