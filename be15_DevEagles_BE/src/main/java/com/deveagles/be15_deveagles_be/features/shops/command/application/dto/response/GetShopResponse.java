package com.deveagles.be15_deveagles_be.features.shops.command.application.dto.response;

import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.Industry;
import com.deveagles.be15_deveagles_be.features.shops.command.domain.aggregate.SNS;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetShopResponse {

  private String shopName;
  private String address;
  private String detailAddress;
  private Long industryId;
  private List<Industry> industryList;
  private String phoneNumber;
  private String bizNumber;
  private List<SNS> snsList;
  private String description;
}
