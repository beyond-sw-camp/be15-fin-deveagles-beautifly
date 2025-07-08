package com.deveagles.be15_deveagles_be.features.customers.query.infrastructure.repository;

import static com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.QAcquisitionChannel.acquisitionChannel;
import static com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.QCustomer.customer;
import static com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.QCustomerGrade.customerGrade;

import com.deveagles.be15_deveagles_be.features.customers.query.dto.response.CustomerDetailResponse;
import com.deveagles.be15_deveagles_be.features.customers.query.repository.CustomerDetailQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomerDetailQueryRepositoryImpl implements CustomerDetailQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<CustomerDetailResponse> findCustomerDetailById(Long customerId, Long shopId) {
    return Optional.ofNullable(
        queryFactory
            .select(
                Projections.constructor(
                    CustomerDetailResponse.class,
                    customer.id,
                    customer.customerName,
                    customer.phoneNumber,
                    customer.memo,
                    customer.birthdate,
                    customer.gender,
                    customer.marketingConsent,
                    customer.notificationConsent,
                    customerGrade.customerGradeName,
                    customer.staffId,
                    acquisitionChannel.channelName,
                    customer.createdAt))
            .from(customer)
            .leftJoin(customerGrade)
            .on(customer.customerGradeId.eq(customerGrade.id))
            .leftJoin(acquisitionChannel)
            .on(customer.channelId.eq(acquisitionChannel.id))
            .where(customer.id.eq(customerId).and(customer.shopId.eq(shopId)))
            .fetchOne());
  }
}
