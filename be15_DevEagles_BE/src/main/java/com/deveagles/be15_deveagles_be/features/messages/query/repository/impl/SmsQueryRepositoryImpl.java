package com.deveagles.be15_deveagles_be.features.messages.query.repository.impl;

import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageDeliveryStatus;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.MessageSendingType;
import com.deveagles.be15_deveagles_be.features.messages.command.domain.aggregate.QSms;
import com.deveagles.be15_deveagles_be.features.messages.query.dto.response.SmsDetailResponse;
import com.deveagles.be15_deveagles_be.features.messages.query.dto.response.SmsListResponse;
import com.deveagles.be15_deveagles_be.features.messages.query.repository.SmsQueryRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
@Slf4j
public class SmsQueryRepositoryImpl implements SmsQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<SmsListResponse> findSmsListByShopId(Long shopId, Pageable pageable) {
    QSms sms = QSms.sms;

    log.info(
        "📨 문자 목록 조회 시작 - shopId={}, page={}, size={}",
        shopId,
        pageable.getPageNumber(),
        pageable.getPageSize());

    BooleanBuilder builder = new BooleanBuilder().and(sms.shopId.eq(shopId));

    // 예약이면 scheduledAt, 아니면 sentAt
    DateTimeExpression<LocalDateTime> dateField =
        new CaseBuilder()
            .when(sms.messageSendingType.eq(MessageSendingType.RESERVATION))
            .then(sms.scheduledAt)
            .otherwise(sms.sentAt);

    List<SmsListResponse> content =
        queryFactory
            .select(
                Projections.constructor(
                    SmsListResponse.class,
                    sms.messageId,
                    sms.messageKind.stringValue(),
                    sms.messageContent,
                    sms.customerId.stringValue(), // 추후 join해서 이름 가져와도 됨
                    sms.messageDeliveryStatus.stringValue(),
                    dateField,
                    new CaseBuilder()
                        .when(sms.messageSendingType.eq(MessageSendingType.RESERVATION))
                        .then(true)
                        .otherwise(false),
                    new CaseBuilder()
                        .when(sms.messageSendingType.eq(MessageSendingType.RESERVATION))
                        .then(true)
                        .otherwise(false),
                    new CaseBuilder()
                        .when(sms.messageDeliveryStatus.eq(MessageDeliveryStatus.FAIL))
                        .then("전송 실패")
                        .otherwise("")))
            .from(sms)
            .where(builder)
            .orderBy(sms.createdAt.desc()) // 정렬 제거 요청 반영
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

    Long total = queryFactory.select(sms.count()).from(sms).where(builder).fetchOne();

    log.info("📨 문자 목록 조회 완료 - 총 {}건", total);

    return new PageImpl<>(content, pageable, total != null ? total : 0);
  }

  @Override
  public Optional<SmsDetailResponse> findSmsDetailByIdAndShopId(Long messageId, Long shopId) {
    QSms sms = QSms.sms;

    SmsDetailResponse result =
        queryFactory
            .select(
                Projections.constructor(
                    SmsDetailResponse.class,
                    sms.messageId,
                    sms.messageContent,
                    sms.messageDeliveryStatus.stringValue(),
                    sms.createdAt,
                    sms.sentAt,
                    sms.scheduledAt,
                    sms.messageType.stringValue(),
                    sms.messageSendingType.stringValue(),
                    sms.messageKind.stringValue(),
                    sms.hasLink,
                    sms.customerId,
                    sms.templateId,
                    sms.customerGradeId,
                    sms.tagId))
            .from(sms)
            .where(sms.messageId.eq(messageId), sms.shopId.eq(shopId))
            .fetchOne();

    return Optional.ofNullable(result);
  }
}
