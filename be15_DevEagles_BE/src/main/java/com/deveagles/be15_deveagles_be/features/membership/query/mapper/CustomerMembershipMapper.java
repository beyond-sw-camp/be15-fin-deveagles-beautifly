package com.deveagles.be15_deveagles_be.features.membership.query.mapper;

import com.deveagles.be15_deveagles_be.features.membership.query.dto.request.CustomerExpiringPrepaidPassFilterRequest;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.request.CustomerExpiringSessionPassFilterRequest;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.request.CustomerMemebershipFilterRequest;
import com.deveagles.be15_deveagles_be.features.membership.query.dto.response.*;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMembershipMapper {
  // 전체 조회용
  List<CustomerMembershipResponse> findAllCustomerMemberships(Long shopId, int offset, int size);

  long countAllCustomerMemberships(Long shopId);

  List<SessionPassInfo> findSessionPassesByCustomerId(Long customerId);

  // 필터 조회용
  List<CustomerMembershipResponse> findCustomerMemberships(
      Long shopId, CustomerMemebershipFilterRequest filter, int offset);

  long countCustomerMemberships(Long shopId, CustomerMemebershipFilterRequest filter);

  // 고객 선불권 조회 만료(예정)
  List<CustomerExpiringPrepaidPassResponse> findExpiringPrepaidPassCustomers(
      Long shopId, CustomerExpiringPrepaidPassFilterRequest request, int offset);

  List<PrepaidPassInfo> findExpiringPrepaidPassesByCustomerId(Long customerId);

  long countExpiringPrepaidPassCustomers(
      Long shopId, CustomerExpiringPrepaidPassFilterRequest request);

  // 고객 횟수권 조회 만료(예정)
  List<CustomerExpiringSessionPassResponse> findExpiringSessionPassCustomers(
      Long shopId, CustomerExpiringSessionPassFilterRequest request, int offset);

  List<SessionPassInfo> findExpiringSessionPassesByCustomerId(Long customerId);

  long countExpiringSessionPassCustomers(
      Long shopId, CustomerExpiringSessionPassFilterRequest request);

  // 고객 선불권 상세 조회
  List<CustomerPrepaidPassDetailInfo> findPrepaidPassDetailsByCustomerId(Long customerId);

  // 고객 횟수권 상세 조회
  List<CustomerSessionPassDetailInfo> findSessionPassDetailsByCustomerId(Long customerId);

  List<CustomerPrepaidPassDetailInfo> findExpiredOrUsedUpPrepaidPasses(Long customerId);

  List<CustomerSessionPassDetailInfo> findExpiredOrUsedUpSessionPasses(Long customerId);
}
