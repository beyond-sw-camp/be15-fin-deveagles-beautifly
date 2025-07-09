package com.deveagles.be15_deveagles_be.features.sales.command.application.service.impl;

import com.deveagles.be15_deveagles_be.common.exception.BusinessException;
import com.deveagles.be15_deveagles_be.common.exception.ErrorCode;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.Customer;
import com.deveagles.be15_deveagles_be.features.customers.command.domain.repository.CustomerRepository;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.CustomerPrepaidPassRepository;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.CustomerSessionPassRepository;
import com.deveagles.be15_deveagles_be.features.sales.command.application.dto.request.ItemSalesRequest;
import com.deveagles.be15_deveagles_be.features.sales.command.application.dto.request.PaymentsInfo;
import com.deveagles.be15_deveagles_be.features.sales.command.application.service.ItemSalesCommandService;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.ItemSales;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.Payments;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.Sales;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.ItemSalesRepository;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.PaymentsRepository;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.repository.SalesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemSalesCommandServiceImpl implements ItemSalesCommandService {

  private final CustomerPrepaidPassRepository customerPrepaidPassRepository;
  private final CustomerSessionPassRepository customerSessionPassRepository;
  private final SalesRepository salesRepository;
  private final PaymentsRepository paymentsRepository;
  private final CustomerRepository customerRepository;
  private final ItemSalesRepository itemSalesRepository;

  @Transactional
  @Override
  public void registItemSales(ItemSalesRequest request) {

    // 유효성 검사
    if (request.getRetailPrice() < 0) {
      throw new BusinessException(ErrorCode.SALES_RETAILPRICE_REQUIRED);
    }
    if (request.getTotalAmount() < 0) {
      throw new BusinessException(ErrorCode.SALES_TOTALAMOUNT_REQUIRED);
    }
    for (PaymentsInfo payment : request.getPayments()) {
      if (payment.getAmount() == null || payment.getAmount() <= 0) {
        throw new BusinessException(ErrorCode.SALES_PAYMENTSAMOUNT_REQUIRED);
      }
      if (payment.getPaymentsMethod() == null) {
        throw new BusinessException(ErrorCode.SALES_PAYMENTMETHOD_REQUIRED);
      }
    }

    // 1. Sales 저장
    Sales sales =
        Sales.builder()
            .shopId(request.getShopId())
            .customerId(request.getCustomerId())
            .staffId(request.getStaffId())
            .reservationId(request.getReservationId())
            .retailPrice(request.getRetailPrice())
            .discountRate(request.getDiscountRate())
            .discountAmount(request.getDiscountAmount())
            .totalAmount(request.getTotalAmount())
            .salesMemo(request.getSalesMemo())
            .salesDate(request.getSalesDate())
            .isRefunded(false)
            .build();
    salesRepository.save(sales);

    // 2. Payments 저장 및 패스 차감
    for (PaymentsInfo p : request.getPayments()) {
      Payments payment =
          Payments.builder()
              .salesId(sales.getSalesId())
              .paymentsMethod(p.getPaymentsMethod())
              .amount(p.getAmount())
              .build();
      paymentsRepository.save(payment);

      switch (p.getPaymentsMethod()) {
        case PREPAID_PASS -> {
          var pass =
              customerPrepaidPassRepository
                  .findById(p.getCustomerPrepaidPassId())
                  .orElseThrow(
                      () -> new BusinessException(ErrorCode.CUSTOMERPREPAIDPASS_NOT_FOUND));
          pass.useAmount(p.getAmount());
        }

        case SESSION_PASS -> {
          var pass =
              customerSessionPassRepository
                  .findById(p.getCustomerSessionPassId())
                  .orElseThrow(
                      () -> new BusinessException(ErrorCode.CUSTOMERSESSIONPASS_NOT_FOUND));

          int useCount = p.getUsedCount() != null ? p.getUsedCount() : 1;
          pass.useCount(useCount);
        }
      }
    }
    // 3. item_sales 저장
    itemSalesRepository.save(
        ItemSales.builder()
            .salesId(sales.getSalesId())
            .secondaryItemId(request.getSecondaryItemId())
            .quantity(request.getQuantity())
            .discountRate(request.getDiscountRate())
            .couponId(request.getCouponId())
            .build());

    // 4. 고객 정보 갱신
    Customer customer =
        customerRepository
            .findById(request.getCustomerId())
            .orElseThrow(() -> new BusinessException(ErrorCode.CUSTOMER_NOT_FOUND));
    customer.incrementVisitCount();
    customer.addRevenue(request.getTotalAmount());
    customer.updateRecentVisitDate(request.getSalesDate().toLocalDate());
  }
}
