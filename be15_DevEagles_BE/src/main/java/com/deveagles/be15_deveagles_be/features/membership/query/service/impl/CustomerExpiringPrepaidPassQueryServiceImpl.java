package com.deveagles.be15_deveagles_be.features.membership.query.service.impl;

import com.deveagles.be15_deveagles_be.common.dto.Pagination;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.request.CustomerExpiringPrepaidPassFilterRequest;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.*;
import com.deveagles.be15_deveagles_be.features.membership.query.mapper.CustomerMembershipMapper;
import com.deveagles.be15_deveagles_be.features.membership.query.service.CustomerExpiringPrepaidPassQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerExpiringPrepaidPassQueryServiceImpl
    implements CustomerExpiringPrepaidPassQueryService {

  private final CustomerMembershipMapper customerMembershipMapper;

  @Override
  public CustomerExpiringPrepaidPassResult getExpiringPrepaidPasses(
      Long shopId, CustomerExpiringPrepaidPassFilterRequest request) {
    int offset = request.getOffset();

    List<CustomerExpiringPrepaidPassResponse> list =
        customerMembershipMapper.findExpiringPrepaidPassCustomers(shopId, request, offset);

    for (CustomerExpiringPrepaidPassResponse customer : list) {
      List<PrepaidPassInfo> prepaidPasses =
          customerMembershipMapper.findExpiringPrepaidPassesByCustomerId(customer.getCustomerId());
      customer.setPrepaidPasses(prepaidPasses);
    }

    long totalItems = customerMembershipMapper.countExpiringPrepaidPassCustomers(shopId, request);

    Pagination pagination =
        Pagination.builder()
            .currentPage(request.getPage())
            .totalPages((int) Math.ceil((double) totalItems / request.getSize()))
            .totalItems(totalItems)
            .build();

    return CustomerExpiringPrepaidPassResult.builder().list(list).pagination(pagination).build();
  }
}
