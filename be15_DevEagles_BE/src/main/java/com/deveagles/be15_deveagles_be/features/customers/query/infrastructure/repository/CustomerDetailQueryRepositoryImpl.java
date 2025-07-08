package com.deveagles.be15_deveagles_be.features.customers.query.infrastructure.repository;

import static com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.QAcquisitionChannel.acquisitionChannel;
import static com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.QCustomer.customer;
import static com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.QCustomerGrade.customerGrade;
import static com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.QTag.tag;
import static com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.QTagByCustomer.tagByCustomer;
import static com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.QStaff.staff;

import com.deveagles.be15_deveagles_be.features.customers.query.dto.response.CustomerDetailResponse;
import com.deveagles.be15_deveagles_be.features.customers.query.repository.CustomerDetailQueryRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomerDetailQueryRepositoryImpl implements CustomerDetailQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Optional<CustomerDetailResponse> findCustomerDetailById(Long customerId, Long shopId) {
    CustomerDetailResponse response =
        queryFactory
            .select(
                Projections.constructor(
                    CustomerDetailResponse.class,
                    customer.id,
                    customer.customerName,
                    customer.phoneNumber,
                    customer.memo,
                    customer.visitCount,
                    customer.totalRevenue,
                    customer.recentVisitDate,
                    customer.birthdate,
                    customer.noshowCount,
                    customer.gender,
                    customer.marketingConsent,
                    customer.marketingConsentedAt,
                    customer.notificationConsent,
                    customer.lastMessageSentAt,
                    customer.createdAt,
                    customer.modifiedAt,
                    customer.shopId,
                    customer.staffId,
                    staff.staffName,
                    customerGrade.id,
                    customerGrade.customerGradeName,
                    customerGrade.discountRate,
                    acquisitionChannel.id,
                    acquisitionChannel.channelName,
                    Expressions.constant(0))) // remainingPrepaidAmount는 현재 구현되지 않음
            .from(customer)
            .leftJoin(customerGrade)
            .on(customer.customerGradeId.eq(customerGrade.id))
            .leftJoin(acquisitionChannel)
            .on(customer.channelId.eq(acquisitionChannel.id))
            .leftJoin(staff)
            .on(customer.staffId.eq(staff.staffId))
            .where(customer.id.eq(customerId).and(customer.shopId.eq(shopId)))
            .fetchOne();

    if (response != null) {
      // 태그 정보 조회 및 설정
      List<CustomerDetailResponse.TagInfo> tags =
          queryFactory
              .select(
                  Projections.constructor(
                      CustomerDetailResponse.TagInfo.class, tag.id, tag.tagName, tag.colorCode))
              .from(tagByCustomer)
              .join(tag)
              .on(tagByCustomer.tagId.eq(tag.id))
              .where(tagByCustomer.customerId.eq(customerId))
              .fetch();

      response.getTags().addAll(tags);
    }

    return Optional.ofNullable(response);
  }
}
