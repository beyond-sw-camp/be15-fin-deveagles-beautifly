package com.deveagles.be15_deveagles_be.features.statistics.query.repository;

import com.deveagles.be15_deveagles_be.features.customers.command.domain.aggregate.QCustomer;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.QPrimaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.QSecondaryItem;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.QItemSales;
import com.deveagles.be15_deveagles_be.features.sales.command.domain.aggregate.QSales;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.QReservation;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.QReservationSetting;
import com.deveagles.be15_deveagles_be.features.schedules.command.domain.aggregate.ReservationStatusName;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.AdvancedSalesStatisticsResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.DailyVisitorStatisticsResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.HourlyVisitorStatisticsResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.ReservationRequest;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.ReservationStatisticsResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.ReservationSummaryResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.SalesStatisticsResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.SalesSummaryResponse;
import com.deveagles.be15_deveagles_be.features.statistics.query.dto.StatisticsRequest;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StatisticsQueryRepositoryImpl implements StatisticsQueryRepository {

  private final JPAQueryFactory queryFactory;
  private final QSales sales = QSales.sales;
  private final QItemSales itemSales = QItemSales.itemSales;
  private final QPrimaryItem primaryItem = QPrimaryItem.primaryItem;
  private final QSecondaryItem secondaryItem = QSecondaryItem.secondaryItem;
  private final QCustomer customer = QCustomer.customer;
  private final QReservation reservation = QReservation.reservation;
  private final QReservationSetting reservationSetting = QReservationSetting.reservationSetting;

  @Override
  public List<SalesStatisticsResponse> findSalesStatisticsByPeriod(
      Long shopId, LocalDate startDate, LocalDate endDate) {
    return queryFactory
        .select(
            sales.salesDate.year(),
            sales.salesDate.month(),
            sales.salesDate.dayOfMonth(),
            sales.totalAmount.sum().coalesce(0),
            sales.count())
        .from(sales)
        .where(
            sales
                .shopId
                .eq(shopId)
                .and(
                    sales.salesDate.between(
                        startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                .and(sales.isRefunded.isFalse()))
        .groupBy(sales.salesDate.year(), sales.salesDate.month(), sales.salesDate.dayOfMonth())
        .orderBy(
            sales.salesDate.year().asc(),
            sales.salesDate.month().asc(),
            sales.salesDate.dayOfMonth().asc())
        .fetch()
        .stream()
        .map(
            tuple ->
                SalesStatisticsResponse.builder()
                    .date(
                        LocalDate.of(
                            tuple.get(0, Integer.class),
                            tuple.get(1, Integer.class),
                            tuple.get(2, Integer.class)))
                    .totalSalesAmount(tuple.get(3, Integer.class).longValue())
                    .totalTransactions(tuple.get(4, Long.class))
                    .build())
        .collect(Collectors.toList());
  }

  @Override
  public List<AdvancedSalesStatisticsResponse> findAdvancedSalesStatistics(
      Long shopId, StatisticsRequest request) {
    // 시간 기반 집계와 상품/카테고리 기반 집계를 완전히 분리
    StatisticsRequest.GroupBy groupBy =
        request.getGroupBy() != null ? request.getGroupBy() : StatisticsRequest.GroupBy.DAY;

    // 시간 기반 집계는 sales 테이블만 사용 (JOIN 없음)
    if (groupBy == StatisticsRequest.GroupBy.DAY
        || groupBy == StatisticsRequest.GroupBy.WEEK
        || groupBy == StatisticsRequest.GroupBy.MONTH) {

      return getTimeBasedStatistics(shopId, request, groupBy);
    }
    // 상품/카테고리 기반 집계만 JOIN 사용
    else {
      return getItemBasedStatistics(shopId, request, groupBy);
    }
  }

  /** 시간 기반 통계 - sales 테이블만 사용하여 정확한 집계 */
  private List<AdvancedSalesStatisticsResponse> getTimeBasedStatistics(
      Long shopId, StatisticsRequest request, StatisticsRequest.GroupBy groupBy) {
    // WHERE 조건 구성
    List<BooleanExpression> whereClauses = new ArrayList<>();
    whereClauses.add(sales.shopId.eq(shopId));
    whereClauses.add(sales.isRefunded.isFalse());
    handleTimeRange(request, whereClauses);

    // 그룹화 키 설정
    StringExpression groupKey;
    switch (groupBy) {
      case DAY:
        groupKey = sales.salesDate.dayOfMonth().stringValue();
        break;
      case WEEK:
        // 연도-주차 형태로 반환 (YYYY-WW)
        groupKey =
            Expressions.stringTemplate(
                "CONCAT({0}, '-', LPAD({1}, 2, '0'))",
                sales.salesDate.year().stringValue(), sales.salesDate.week().stringValue());
        break;
      case MONTH:
        // 연도-월 형태로 반환 (YYYY-MM)
        groupKey =
            Expressions.stringTemplate(
                "CONCAT({0}, '-', LPAD({1}, 2, '0'))",
                sales.salesDate.year().stringValue(), sales.salesDate.month().stringValue());
        break;
      default:
        throw new IllegalArgumentException("Invalid time groupBy: " + groupBy);
    }

    return queryFactory
        .select(
            groupKey,
            sales.totalAmount.sum().coalesce(0),
            sales.count().coalesce(0L),
            sales.discountAmount.sum().coalesce(0),
            Expressions.constant(0))
        .from(sales)
        .where(whereClauses.toArray(new BooleanExpression[0]))
        .groupBy(groupKey)
        .orderBy(groupKey.asc())
        .fetch()
        .stream()
        .map(
            tuple ->
                AdvancedSalesStatisticsResponse.builder()
                    .date(tuple.get(0, String.class))
                    .gender(null)
                    .category(null)
                    .primaryItemName(null)
                    .secondaryItemName(null)
                    .totalSalesAmount(tuple.get(1, Integer.class).longValue())
                    .totalTransactions(tuple.get(2, Long.class))
                    .totalDiscountAmount(tuple.get(3, Integer.class).longValue())
                    .totalCouponDiscountAmount(tuple.get(4, Integer.class).longValue())
                    .build())
        .collect(Collectors.toList());
  }

  /** 상품/카테고리 기반 통계 - JOIN 사용하여 상품별 실제 금액 계산 */
  private List<AdvancedSalesStatisticsResponse> getItemBasedStatistics(
      Long shopId, StatisticsRequest request, StatisticsRequest.GroupBy groupBy) {
    JPAQuery<?> query = queryFactory.from(sales);

    query.leftJoin(itemSales).on(sales.salesId.eq(itemSales.salesId));
    query.leftJoin(secondaryItem).on(itemSales.secondaryItemId.eq(secondaryItem.secondaryItemId));
    query.leftJoin(primaryItem).on(secondaryItem.primaryItemId.eq(primaryItem.primaryItemId));

    if (groupBy == StatisticsRequest.GroupBy.GENDER) {
      query.leftJoin(customer).on(sales.customerId.eq(customer.id));
    }

    List<BooleanExpression> whereClauses = new ArrayList<>();
    whereClauses.add(sales.shopId.eq(shopId));
    whereClauses.add(sales.isRefunded.isFalse());

    whereClauses.add(itemSales.salesId.isNotNull());
    whereClauses.add(secondaryItem.secondaryItemId.isNotNull());
    whereClauses.add(primaryItem.primaryItemId.isNotNull());

    handleTimeRange(request, whereClauses);

    if (request.getGender() != null && groupBy == StatisticsRequest.GroupBy.GENDER) {
      whereClauses.add(
          customer.gender.isNotNull().and(customer.gender.stringValue().eq(request.getGender())));
    }
    if (request.getCategoryId() != null) {
      whereClauses.add(primaryItem.category.stringValue().eq(request.getCategoryId().toString()));
    }
    if (request.getPrimaryItemId() != null) {
      whereClauses.add(primaryItem.primaryItemId.eq(request.getPrimaryItemId()));
    }
    if (request.getSecondaryItemId() != null) {
      whereClauses.add(secondaryItem.secondaryItemId.eq(request.getSecondaryItemId()));
    }

    query.where(whereClauses.toArray(new BooleanExpression[0]));

    // 그룹화 키 설정
    StringExpression groupKey;
    switch (groupBy) {
      case GENDER:
        groupKey =
            new CaseBuilder()
                .when(customer.gender.isNotNull())
                .then(customer.gender.stringValue())
                .otherwise("UNKNOWN");
        break;
      case CATEGORY:
        groupKey = primaryItem.category.stringValue().coalesce("UNKNOWN");
        break;
      case PRIMARY_ITEM:
        groupKey = primaryItem.primaryItemName.coalesce("UNKNOWN");
        break;
      case SECONDARY_ITEM:
        groupKey = secondaryItem.secondaryItemName.coalesce("UNKNOWN");
        break;
      default:
        throw new IllegalArgumentException("Unsupported groupBy: " + groupBy);
    }

    return query
        .groupBy(groupKey)
        .select(
            groupKey,
            itemSales.quantity.multiply(secondaryItem.secondaryItemPrice).sum().coalesce(0),
            sales.salesId.countDistinct().coalesce(0L),
            Expressions.numberTemplate(
                Integer.class,
                "COALESCE(SUM(CASE WHEN {0} > 0 THEN "
                    + "CAST(({1} * {2} * {3}) / {0} AS INTEGER) ELSE 0 END), 0)",
                sales.totalAmount,
                itemSales.quantity,
                secondaryItem.secondaryItemPrice,
                sales.discountAmount),
            Expressions.numberTemplate(
                Integer.class,
                "COALESCE(SUM(CASE WHEN {0} > 0 AND {4} IS NOT NULL THEN "
                    + "CAST(({1} * {2} * {3}) / {0} AS INTEGER) ELSE 0 END), 0)",
                sales.totalAmount,
                itemSales.quantity,
                secondaryItem.secondaryItemPrice,
                sales.discountAmount,
                itemSales.couponId))
        .fetch()
        .stream()
        .map(
            tuple ->
                AdvancedSalesStatisticsResponse.builder()
                    .date(null)
                    .gender(
                        groupBy == StatisticsRequest.GroupBy.GENDER
                            ? tuple.get(0, String.class)
                            : null)
                    .category(
                        groupBy == StatisticsRequest.GroupBy.CATEGORY
                            ? tuple.get(0, String.class)
                            : null)
                    .primaryItemName(
                        groupBy == StatisticsRequest.GroupBy.PRIMARY_ITEM
                            ? tuple.get(0, String.class)
                            : null)
                    .secondaryItemName(
                        groupBy == StatisticsRequest.GroupBy.SECONDARY_ITEM
                            ? tuple.get(0, String.class)
                            : null)
                    .totalSalesAmount(tuple.get(1, Integer.class).longValue())
                    .totalTransactions(tuple.get(2, Long.class))
                    .totalDiscountAmount(tuple.get(3, Integer.class).longValue())
                    .totalCouponDiscountAmount(tuple.get(4, Integer.class).longValue())
                    .build())
        .collect(Collectors.toList());
  }

  private void handleTimeRange(StatisticsRequest request, List<BooleanExpression> whereClauses) {
    LocalDate now = LocalDate.now();
    if (request.getTimeRange() != null) {
      switch (request.getTimeRange()) {
        case LAST_WEEK:
          whereClauses.add(
              sales.salesDate.between(now.minusWeeks(1).atStartOfDay(), now.atTime(LocalTime.MAX)));
          break;
        case LAST_MONTH:
          whereClauses.add(
              sales.salesDate.between(
                  now.minusMonths(1).atStartOfDay(), now.atTime(LocalTime.MAX)));
          break;
        case LAST_6_MONTHS:
          whereClauses.add(
              sales.salesDate.between(
                  now.minusMonths(6).atStartOfDay(), now.atTime(LocalTime.MAX)));
          break;
        case LAST_YEAR:
          whereClauses.add(
              sales.salesDate.between(now.minusYears(1).atStartOfDay(), now.atTime(LocalTime.MAX)));
          break;
        case CUSTOM:
          if (request.getStartDate() != null && request.getEndDate() != null) {
            whereClauses.add(
                sales.salesDate.between(
                    request.getStartDate().atStartOfDay(),
                    request.getEndDate().atTime(LocalTime.MAX)));
          }
          break;
      }
    }
  }

  @Override
  public SalesSummaryResponse findSalesSummary(
      Long shopId, LocalDate startDate, LocalDate endDate) {
    // Get total sales and transaction count for the period
    var basicStats =
        queryFactory
            .select(sales.totalAmount.sum().coalesce(0), sales.count())
            .from(sales)
            .where(
                sales
                    .shopId
                    .eq(shopId)
                    .and(
                        sales.salesDate.between(
                            startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                    .and(sales.isRefunded.isFalse()))
            .fetchOne();

    Long totalSales = basicStats.get(0, Integer.class).longValue();
    Long totalTransactions = basicStats.get(1, Long.class);

    // Calculate daily average
    long daysBetween = startDate.until(endDate).getDays() + 1;
    double dailyAverage = daysBetween > 0 ? (double) totalSales / daysBetween : 0.0;

    // Calculate average order value
    double averageOrderValue =
        totalTransactions > 0 ? (double) totalSales / totalTransactions : 0.0;

    return SalesSummaryResponse.builder()
        .totalSales(totalSales)
        .dailyAverage(dailyAverage)
        .totalTransactions(totalTransactions)
        .averageOrderValue(averageOrderValue)
        .startDate(startDate)
        .endDate(endDate)
        .build();
  }

  @Override
  public List<ReservationStatisticsResponse> findReservationStatisticsByPeriod(
      Long shopId, LocalDate startDate, LocalDate endDate) {

    return getDailyReservationStatistics(shopId, startDate, endDate);
  }

  @Override
  public List<ReservationStatisticsResponse> findAdvancedReservationStatistics(
      Long shopId, ReservationRequest request) {

    LocalDate startDate = request.getStartDate();
    LocalDate endDate = request.getEndDate();
    ReservationRequest.GroupBy groupBy =
        request.getGroupBy() != null ? request.getGroupBy() : ReservationRequest.GroupBy.DAY;

    switch (groupBy) {
      case DAY:
        return getDailyReservationStatistics(shopId, startDate, endDate);
      case WEEK:
        return getWeeklyReservationStatistics(shopId, startDate, endDate);
      case MONTH:
        return getMonthlyReservationStatistics(shopId, startDate, endDate);
      case TIME_SLOT:
        return getTimeSlotReservationStatistics(shopId, startDate, endDate);
      case HOUR:
        return getHourlyReservationStatistics(shopId, startDate, endDate);
      case STAFF:
        return getStaffBasedReservationStatistics(shopId, startDate, endDate, request.getStaffId());
      case DAY_TIME_SLOT:
        return getDayTimeSlotReservationStatistics(shopId, startDate, endDate);
      default:
        throw new IllegalArgumentException("Invalid groupBy: " + groupBy);
    }
  }

  @Override
  public ReservationSummaryResponse findReservationSummary(
      Long shopId, LocalDate startDate, LocalDate endDate) {

    // 기간 내 총 가능한 슬롯 수 계산
    int totalSlots = calculateTotalSlotsForPeriod(shopId, startDate, endDate);

    // 실제 예약된 슬롯 수 계산
    Long reservedCount =
        queryFactory
            .select(reservation.count())
            .from(reservation)
            .where(
                reservation
                    .shopId
                    .eq(shopId)
                    .and(
                        reservation.reservationStartAt.between(
                            startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                    .and(reservation.deletedAt.isNull())
                    .and(
                        reservation.reservationStatusName.notIn(
                            ReservationStatusName.CBC, ReservationStatusName.CBS)))
            .fetchOne();

    int reservedSlots = reservedCount != null ? reservedCount.intValue() : 0;
    int availableSlots = totalSlots - reservedSlots;
    BigDecimal averageReservationRate =
        totalSlots > 0
            ? BigDecimal.valueOf(reservedSlots * 100.0 / totalSlots)
                .setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

    long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;

    // 피크 시간대 예약 통계 (예: 오전 10시-오후 6시)
    Long peakHourReservations =
        queryFactory
            .select(reservation.count())
            .from(reservation)
            .where(
                reservation
                    .shopId
                    .eq(shopId)
                    .and(
                        reservation.reservationStartAt.between(
                            startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                    .and(reservation.reservationStartAt.hour().between(10, 17))
                    .and(reservation.deletedAt.isNull())
                    .and(
                        reservation.reservationStatusName.notIn(
                            ReservationStatusName.CBC, ReservationStatusName.CBS)))
            .fetchOne();

    int peakHourSlots = peakHourReservations != null ? peakHourReservations.intValue() : 0;
    int offPeakHourSlots = reservedSlots - peakHourSlots;

    // 피크 시간대 총 가능 슬롯 수 (8시간 * 2슬롯 * 일수)
    int totalPeakSlots = (int) (8 * 2 * daysBetween);
    int totalOffPeakSlots = totalSlots - totalPeakSlots;

    BigDecimal peakHourReservationRate =
        totalPeakSlots > 0
            ? BigDecimal.valueOf(peakHourSlots * 100.0 / totalPeakSlots)
                .setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

    BigDecimal offPeakHourReservationRate =
        totalOffPeakSlots > 0
            ? BigDecimal.valueOf(offPeakHourSlots * 100.0 / totalOffPeakSlots)
                .setScale(2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

    return new ReservationSummaryResponse(
        totalSlots,
        reservedSlots,
        availableSlots,
        averageReservationRate,
        (int) daysBetween,
        BigDecimal.valueOf(reservedSlots * 1.0 / daysBetween).setScale(2, RoundingMode.HALF_UP),
        peakHourSlots,
        offPeakHourSlots,
        peakHourReservationRate,
        offPeakHourReservationRate);
  }

  private List<ReservationStatisticsResponse> getDailyReservationStatistics(
      Long shopId, LocalDate startDate, LocalDate endDate) {

    List<ReservationStatisticsResponse> result = new ArrayList<>();
    LocalDate currentDate = startDate;

    while (!currentDate.isAfter(endDate)) {
      // 해당 날짜의 예약 설정 조회
      List<Integer> availableDays =
          queryFactory
              .select(reservationSetting.id.availableDay)
              .from(reservationSetting)
              .where(
                  reservationSetting
                      .id
                      .shopId
                      .eq(shopId)
                      .and(reservationSetting.deletedAt.isNull()))
              .fetch();

      int dayOfWeek = currentDate.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday

      if (availableDays.contains(dayOfWeek)) {
        // 해당 날짜의 운영 시간 조회
        var setting =
            queryFactory
                .select(
                    reservationSetting.availableStartTime,
                    reservationSetting.availableEndTime,
                    reservationSetting.lunchStartTime,
                    reservationSetting.lunchEndTime)
                .from(reservationSetting)
                .where(
                    reservationSetting
                        .id
                        .shopId
                        .eq(shopId)
                        .and(reservationSetting.id.availableDay.eq(dayOfWeek))
                        .and(reservationSetting.deletedAt.isNull()))
                .fetchOne();

        if (setting != null) {
          LocalTime startTime = setting.get(0, LocalTime.class);
          LocalTime endTime = setting.get(1, LocalTime.class);
          LocalTime lunchStart = setting.get(2, LocalTime.class);
          LocalTime lunchEnd = setting.get(3, LocalTime.class);

          // 총 가능한 슬롯 수 계산 (30분 단위)
          int totalSlots = calculateTotalSlots(startTime, endTime, lunchStart, lunchEnd);

          // 해당 날짜의 실제 예약 수 조회
          Long reservedCount =
              queryFactory
                  .select(reservation.count())
                  .from(reservation)
                  .where(
                      reservation
                          .shopId
                          .eq(shopId)
                          .and(
                              reservation.reservationStartAt.between(
                                  currentDate.atStartOfDay(), currentDate.atTime(LocalTime.MAX)))
                          .and(reservation.deletedAt.isNull())
                          .and(
                              reservation.reservationStatusName.notIn(
                                  ReservationStatusName.CBC,
                                  ReservationStatusName.CBS))) // 고객취소, 매장취소 제외
                  .fetchOne();

          int reservedSlots = reservedCount != null ? reservedCount.intValue() : 0;
          int availableSlots = totalSlots - reservedSlots;
          BigDecimal reservationRate =
              totalSlots > 0
                  ? BigDecimal.valueOf(reservedSlots * 100.0 / totalSlots)
                      .setScale(2, RoundingMode.HALF_UP)
                  : BigDecimal.ZERO;

          result.add(
              new ReservationStatisticsResponse(
                  currentDate,
                  LocalTime.of(0, 0),
                  totalSlots,
                  reservedSlots,
                  availableSlots,
                  reservationRate,
                  "DAY",
                  currentDate.toString(),
                  null,
                  null));
        }
      } else {
        // 운영하지 않는 날
        result.add(
            new ReservationStatisticsResponse(
                currentDate,
                LocalTime.of(0, 0),
                0,
                0,
                0,
                BigDecimal.ZERO,
                "DAY",
                currentDate.toString(),
                null,
                null));
      }

      currentDate = currentDate.plusDays(1);
    }

    return result;
  }

  private List<ReservationStatisticsResponse> getWeeklyReservationStatistics(
      Long shopId, LocalDate startDate, LocalDate endDate) {

    List<ReservationStatisticsResponse> result = new ArrayList<>();
    LocalDate weekStart = startDate.with(java.time.DayOfWeek.MONDAY);

    while (!weekStart.isAfter(endDate)) {
      LocalDate weekEnd = weekStart.plusDays(6);
      if (weekEnd.isAfter(endDate)) weekEnd = endDate;

      // 해당 주의 총 가능한 슬롯 수 계산
      int totalSlots = calculateTotalSlotsForPeriod(shopId, weekStart, weekEnd);

      // 해당 주의 실제 예약 수 조회
      Long reservedCount =
          queryFactory
              .select(reservation.count())
              .from(reservation)
              .where(
                  reservation
                      .shopId
                      .eq(shopId)
                      .and(
                          reservation.reservationStartAt.between(
                              weekStart.atStartOfDay(), weekEnd.atTime(LocalTime.MAX)))
                      .and(reservation.deletedAt.isNull())
                      .and(
                          reservation.reservationStatusName.notIn(
                              ReservationStatusName.CBC, ReservationStatusName.CBS)))
              .fetchOne();

      int reservedSlots = reservedCount != null ? reservedCount.intValue() : 0;
      int availableSlots = totalSlots - reservedSlots;
      BigDecimal reservationRate =
          totalSlots > 0
              ? BigDecimal.valueOf(reservedSlots * 100.0 / totalSlots)
                  .setScale(2, RoundingMode.HALF_UP)
              : BigDecimal.ZERO;

      // 연도-주차 형태로 키 생성 (YYYY-WW) - ISO 8601 주차 기준
      int year = weekStart.getYear();
      int weekOfYear = weekStart.get(java.time.temporal.WeekFields.ISO.weekOfYear());
      String weekKey = String.format("%d-%02d", year, weekOfYear);

      result.add(
          new ReservationStatisticsResponse(
              weekStart,
              LocalTime.of(0, 0),
              totalSlots,
              reservedSlots,
              availableSlots,
              reservationRate,
              "WEEK",
              weekKey,
              null,
              null));

      weekStart = weekStart.plusWeeks(1);
    }

    return result;
  }

  private List<ReservationStatisticsResponse> getMonthlyReservationStatistics(
      Long shopId, LocalDate startDate, LocalDate endDate) {

    List<ReservationStatisticsResponse> result = new ArrayList<>();
    LocalDate monthStart = startDate.withDayOfMonth(1);

    while (!monthStart.isAfter(endDate)) {
      LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);
      if (monthEnd.isAfter(endDate)) monthEnd = endDate;

      // 해당 월의 총 가능한 슬롯 수 계산
      int totalSlots = calculateTotalSlotsForPeriod(shopId, monthStart, monthEnd);

      // 해당 월의 실제 예약 수 조회
      Long reservedCount =
          queryFactory
              .select(reservation.count())
              .from(reservation)
              .where(
                  reservation
                      .shopId
                      .eq(shopId)
                      .and(
                          reservation.reservationStartAt.between(
                              monthStart.atStartOfDay(), monthEnd.atTime(LocalTime.MAX)))
                      .and(reservation.deletedAt.isNull())
                      .and(
                          reservation.reservationStatusName.notIn(
                              ReservationStatusName.CBC, ReservationStatusName.CBS)))
              .fetchOne();

      int reservedSlots = reservedCount != null ? reservedCount.intValue() : 0;
      int availableSlots = totalSlots - reservedSlots;
      BigDecimal reservationRate =
          totalSlots > 0
              ? BigDecimal.valueOf(reservedSlots * 100.0 / totalSlots)
                  .setScale(2, RoundingMode.HALF_UP)
              : BigDecimal.ZERO;

      // 연도-월 형태로 키 생성 (YYYY-MM)
      int year = monthStart.getYear();
      int month = monthStart.getMonthValue();
      String monthKey = String.format("%d-%02d", year, month);

      result.add(
          new ReservationStatisticsResponse(
              monthStart,
              LocalTime.of(0, 0),
              totalSlots,
              reservedSlots,
              availableSlots,
              reservationRate,
              "MONTH",
              monthKey,
              null,
              null));

      monthStart = monthStart.plusMonths(1);
    }

    return result;
  }

  private List<ReservationStatisticsResponse> getTimeSlotReservationStatistics(
      Long shopId, LocalDate startDate, LocalDate endDate) {

    List<ReservationStatisticsResponse> result = new ArrayList<>();
    long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;

    // 30분 단위로 하루 48개 슬롯 생성
    for (int slot = 0; slot < 48; slot++) {
      LocalTime timeSlot = LocalTime.of(slot / 2, (slot % 2) * 30);
      LocalTime timeSlotEnd = timeSlot.plusMinutes(30);

      // 해당 시간대의 예약 수 조회
      Long reservedCount =
          queryFactory
              .select(reservation.count())
              .from(reservation)
              .where(
                  reservation
                      .shopId
                      .eq(shopId)
                      .and(
                          reservation.reservationStartAt.between(
                              startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                      .and(reservation.reservationStartAt.hour().eq(timeSlot.getHour()))
                      .and(
                          reservation
                              .reservationStartAt
                              .minute()
                              .between(timeSlot.getMinute(), timeSlotEnd.getMinute() - 1))
                      .and(reservation.deletedAt.isNull())
                      .and(
                          reservation.reservationStatusName.notIn(
                              ReservationStatusName.CBC, ReservationStatusName.CBS)))
              .fetchOne();

      int reservedSlots = reservedCount != null ? reservedCount.intValue() : 0;
      int totalSlots = (int) daysBetween; // 해당 시간대의 총 가능한 슬롯 수
      int availableSlots = totalSlots - reservedSlots;
      BigDecimal reservationRate =
          totalSlots > 0
              ? BigDecimal.valueOf(reservedSlots * 100.0 / totalSlots)
                  .setScale(2, RoundingMode.HALF_UP)
              : BigDecimal.ZERO;

      result.add(
          new ReservationStatisticsResponse(
              startDate,
              timeSlot,
              totalSlots,
              reservedSlots,
              availableSlots,
              reservationRate,
              "TIME_SLOT",
              timeSlot.toString(),
              null,
              null));
    }

    return result;
  }

  private List<ReservationStatisticsResponse> getHourlyReservationStatistics(
      Long shopId, LocalDate startDate, LocalDate endDate) {

    List<ReservationStatisticsResponse> result = new ArrayList<>();
    long daysBetween = ChronoUnit.DAYS.between(startDate, endDate) + 1;

    // 24시간 단위로 집계
    for (int hour = 0; hour < 24; hour++) {
      LocalTime hourSlot = LocalTime.of(hour, 0);

      // 해당 시간대의 예약 수 조회
      Long reservedCount =
          queryFactory
              .select(reservation.count())
              .from(reservation)
              .where(
                  reservation
                      .shopId
                      .eq(shopId)
                      .and(
                          reservation.reservationStartAt.between(
                              startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                      .and(reservation.reservationStartAt.hour().eq(hour))
                      .and(reservation.deletedAt.isNull())
                      .and(
                          reservation.reservationStatusName.notIn(
                              ReservationStatusName.CBC, ReservationStatusName.CBS)))
              .fetchOne();

      int reservedSlots = reservedCount != null ? reservedCount.intValue() : 0;
      int totalSlots = (int) daysBetween * 2; // 시간당 2개 슬롯 (30분 단위)
      int availableSlots = totalSlots - reservedSlots;
      BigDecimal reservationRate =
          totalSlots > 0
              ? BigDecimal.valueOf(reservedSlots * 100.0 / totalSlots)
                  .setScale(2, RoundingMode.HALF_UP)
              : BigDecimal.ZERO;

      result.add(
          new ReservationStatisticsResponse(
              startDate,
              hourSlot,
              totalSlots,
              reservedSlots,
              availableSlots,
              reservationRate,
              "HOUR",
              hourSlot.toString(),
              null,
              null));
    }

    return result;
  }

  @Override
  public List<ReservationStatisticsResponse> findStaffReservationStatistics(
      Long shopId, Long staffId, LocalDate startDate, LocalDate endDate) {

    List<ReservationStatisticsResponse> result = new ArrayList<>();
    LocalDate currentDate = startDate;

    while (!currentDate.isAfter(endDate)) {
      // 해당 날짜의 예약 설정 조회
      List<Integer> availableDays =
          queryFactory
              .select(reservationSetting.id.availableDay)
              .from(reservationSetting)
              .where(
                  reservationSetting
                      .id
                      .shopId
                      .eq(shopId)
                      .and(reservationSetting.deletedAt.isNull()))
              .fetch();

      int dayOfWeek = currentDate.getDayOfWeek().getValue();

      if (availableDays.contains(dayOfWeek)) {
        // 해당 날짜의 운영 시간 조회
        var setting =
            queryFactory
                .select(
                    reservationSetting.availableStartTime,
                    reservationSetting.availableEndTime,
                    reservationSetting.lunchStartTime,
                    reservationSetting.lunchEndTime)
                .from(reservationSetting)
                .where(
                    reservationSetting
                        .id
                        .shopId
                        .eq(shopId)
                        .and(reservationSetting.id.availableDay.eq(dayOfWeek))
                        .and(reservationSetting.deletedAt.isNull()))
                .fetchOne();

        if (setting != null) {
          LocalTime startTime = setting.get(0, LocalTime.class);
          LocalTime endTime = setting.get(1, LocalTime.class);
          LocalTime lunchStart = setting.get(2, LocalTime.class);
          LocalTime lunchEnd = setting.get(3, LocalTime.class);

          // 총 가능한 슬롯 수 계산 (30분 단위)
          int totalSlots = calculateTotalSlots(startTime, endTime, lunchStart, lunchEnd);

          // 해당 날짜의 특정 직원 예약 수 조회
          Long reservedCount =
              queryFactory
                  .select(reservation.count())
                  .from(reservation)
                  .where(
                      reservation
                          .shopId
                          .eq(shopId)
                          .and(reservation.staffId.eq(staffId))
                          .and(
                              reservation.reservationStartAt.between(
                                  currentDate.atStartOfDay(), currentDate.atTime(LocalTime.MAX)))
                          .and(reservation.deletedAt.isNull())
                          .and(
                              reservation.reservationStatusName.notIn(
                                  ReservationStatusName.CBC,
                                  ReservationStatusName.CBS))) // 고객취소, 매장취소 제외
                  .fetchOne();

          int reservedSlots = reservedCount != null ? reservedCount.intValue() : 0;
          int availableSlots = totalSlots - reservedSlots;
          BigDecimal reservationRate =
              totalSlots > 0
                  ? BigDecimal.valueOf(reservedSlots * 100.0 / totalSlots)
                      .setScale(2, RoundingMode.HALF_UP)
                  : BigDecimal.ZERO;

          result.add(
              new ReservationStatisticsResponse(
                  currentDate,
                  LocalTime.of(0, 0),
                  totalSlots,
                  reservedSlots,
                  availableSlots,
                  reservationRate,
                  "STAFF",
                  currentDate.toString(),
                  staffId,
                  "직원" + staffId // 실제로는 직원 이름을 조회해야 함
                  ));
        }
      } else {
        // 운영하지 않는 날
        result.add(
            new ReservationStatisticsResponse(
                currentDate,
                LocalTime.of(0, 0),
                0,
                0,
                0,
                BigDecimal.ZERO,
                "STAFF",
                currentDate.toString(),
                staffId,
                "직원" + staffId));
      }

      currentDate = currentDate.plusDays(1);
    }

    return result;
  }

  @Override
  public List<ReservationStatisticsResponse> findAllStaffReservationStatistics(
      Long shopId, LocalDate startDate, LocalDate endDate) {

    List<ReservationStatisticsResponse> result = new ArrayList<>();

    // 해당 샵의 모든 직원 조회 (예약이 있는 직원만)
    List<Long> staffIds =
        queryFactory
            .select(reservation.staffId)
            .from(reservation)
            .where(
                reservation
                    .shopId
                    .eq(shopId)
                    .and(
                        reservation.reservationStartAt.between(
                            startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                    .and(reservation.deletedAt.isNull())
                    .and(
                        reservation.reservationStatusName.notIn(
                            ReservationStatusName.CBC, ReservationStatusName.CBS)))
            .distinct()
            .fetch();

    for (Long staffId : staffIds) {
      // 기간 내 총 가능한 슬롯 수 계산
      int totalSlots = calculateTotalSlotsForPeriod(shopId, startDate, endDate);

      // 해당 직원의 예약 수 조회
      Long reservedCount =
          queryFactory
              .select(reservation.count())
              .from(reservation)
              .where(
                  reservation
                      .shopId
                      .eq(shopId)
                      .and(reservation.staffId.eq(staffId))
                      .and(
                          reservation.reservationStartAt.between(
                              startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                      .and(reservation.deletedAt.isNull())
                      .and(
                          reservation.reservationStatusName.notIn(
                              ReservationStatusName.CBC, ReservationStatusName.CBS)))
              .fetchOne();

      int reservedSlots = reservedCount != null ? reservedCount.intValue() : 0;
      int availableSlots = totalSlots - reservedSlots;
      BigDecimal reservationRate =
          totalSlots > 0
              ? BigDecimal.valueOf(reservedSlots * 100.0 / totalSlots)
                  .setScale(2, RoundingMode.HALF_UP)
              : BigDecimal.ZERO;

      result.add(
          new ReservationStatisticsResponse(
              startDate,
              LocalTime.of(0, 0),
              totalSlots,
              reservedSlots,
              availableSlots,
              reservationRate,
              "STAFF",
              "직원" + staffId,
              staffId,
              "직원" + staffId));
    }

    return result;
  }

  private List<ReservationStatisticsResponse> getStaffBasedReservationStatistics(
      Long shopId, LocalDate startDate, LocalDate endDate, Long staffId) {

    if (staffId != null) {
      return findStaffReservationStatistics(shopId, staffId, startDate, endDate);
    } else {
      return findAllStaffReservationStatistics(shopId, startDate, endDate);
    }
  }

  /**
   * 요일별/시간대별 히트맵용 예약율 통계 조회
   *
   * @param shopId 매장 ID
   * @param startDate 시작일
   * @param endDate 종료일
   * @return 요일별/시간대별 예약율 데이터
   */
  private List<ReservationStatisticsResponse> getDayTimeSlotReservationStatistics(
      Long shopId, LocalDate startDate, LocalDate endDate) {

    System.out.println("🔍 [DEBUG] getDayTimeSlotReservationStatistics 시작");
    System.out.println(
        "🔍 [DEBUG] shopId: " + shopId + ", startDate: " + startDate + ", endDate: " + endDate);

    // 전체 예약 데이터 개수 확인
    Long totalReservationCount =
        queryFactory
            .select(reservation.count())
            .from(reservation)
            .where(
                reservation
                    .shopId
                    .eq(shopId)
                    .and(
                        reservation.reservationStartAt.between(
                            startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                    .and(reservation.deletedAt.isNull())
                    .and(
                        reservation.reservationStatusName.notIn(
                            ReservationStatusName.CBC, ReservationStatusName.CBS)))
            .fetchOne();

    System.out.println(
        "🔍 [DEBUG] 기간 내 총 예약 건수: " + (totalReservationCount != null ? totalReservationCount : 0));

    // 예약 설정 확인
    Long settingCount =
        queryFactory
            .select(reservationSetting.count())
            .from(reservationSetting)
            .where(
                reservationSetting.id.shopId.eq(shopId).and(reservationSetting.deletedAt.isNull()))
            .fetchOne();

    System.out.println("🔍 [DEBUG] 예약 설정 개수: " + (settingCount != null ? settingCount : 0));

    List<ReservationStatisticsResponse> result = new ArrayList<>();

    // 각 요일별로 시간대별 예약율 계산
    for (int dayOfWeek = 1; dayOfWeek <= 7; dayOfWeek++) { // 1=Monday, 7=Sunday

      // 해당 요일의 운영 시간 조회
      System.out.println("🔍 [DEBUG] 요일 " + dayOfWeek + " 운영시간 조회 중...");
      var setting =
          queryFactory
              .select(
                  reservationSetting.availableStartTime,
                  reservationSetting.availableEndTime,
                  reservationSetting.lunchStartTime,
                  reservationSetting.lunchEndTime)
              .from(reservationSetting)
              .where(
                  reservationSetting
                      .id
                      .shopId
                      .eq(shopId)
                      .and(reservationSetting.id.availableDay.eq(dayOfWeek))
                      .and(reservationSetting.deletedAt.isNull()))
              .fetchOne();

      if (setting != null) {
        System.out.println(
            "🔍 [DEBUG] 요일 "
                + dayOfWeek
                + " 운영시간 찾음: "
                + setting.get(0, LocalTime.class)
                + " ~ "
                + setting.get(1, LocalTime.class));
        LocalTime startTime = setting.get(0, LocalTime.class);
        LocalTime endTime = setting.get(1, LocalTime.class);
        LocalTime lunchStart = setting.get(2, LocalTime.class);
        LocalTime lunchEnd = setting.get(3, LocalTime.class);

        // 9시부터 20시까지 각 시간대별 예약율 계산
        for (int hour = 9; hour <= 20; hour++) {

          // 해당 요일과 시간대의 총 예약 가능 일수 계산
          int totalDays = 0;
          LocalDate currentDate = startDate;
          while (!currentDate.isAfter(endDate)) {
            if (currentDate.getDayOfWeek().getValue() == dayOfWeek) {
              totalDays++;
            }
            currentDate = currentDate.plusDays(1);
          }

          // 해당 요일과 시간대의 실제 예약 수 조회
          Long reservedCount =
              queryFactory
                  .select(reservation.count())
                  .from(reservation)
                  .where(
                      reservation
                          .shopId
                          .eq(shopId)
                          .and(
                              reservation.reservationStartAt.between(
                                  startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                          .and(reservation.reservationStartAt.hour().eq(hour))
                          .and(reservation.reservationStartAt.dayOfWeek().eq(dayOfWeek))
                          .and(reservation.deletedAt.isNull())
                          .and(
                              reservation.reservationStatusName.notIn(
                                  ReservationStatusName.CBC, ReservationStatusName.CBS)))
                  .fetchOne();

          // 요일 조건 없이 해당 시간대 전체 예약 수도 조회 (비교용)
          if (hour == 10 && dayOfWeek == 1) {
            Long allHourReservations =
                queryFactory
                    .select(reservation.count())
                    .from(reservation)
                    .where(
                        reservation
                            .shopId
                            .eq(shopId)
                            .and(
                                reservation.reservationStartAt.between(
                                    startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                            .and(reservation.reservationStartAt.hour().eq(hour))
                            .and(reservation.deletedAt.isNull())
                            .and(
                                reservation.reservationStatusName.notIn(
                                    ReservationStatusName.CBC, ReservationStatusName.CBS)))
                    .fetchOne();
            System.out.println(
                "🔍 [DEBUG] "
                    + hour
                    + "시 전체 예약 수 (요일 조건 없음): "
                    + (allHourReservations != null ? allHourReservations : 0));
          }

          int reservedSlots = reservedCount != null ? reservedCount.intValue() : 0;

          // 해당 시간대가 운영시간 내인지 확인
          boolean isOperatingHour =
              (hour >= startTime.getHour() && hour <= endTime.getHour())
                  && !(lunchStart != null
                      && lunchEnd != null
                      && hour >= lunchStart.getHour()
                      && hour < lunchEnd.getHour());

          int totalSlots = isOperatingHour ? totalDays * 2 : 0; // 시간당 2개 슬롯 (30분 단위)

          if (hour == 10 && dayOfWeek == 1) { // 월요일 10시만 샘플 로깅
            System.out.println(
                "🔍 [DEBUG] 샘플 조회 (월요일 10시): totalDays="
                    + totalDays
                    + ", reservedSlots="
                    + reservedSlots
                    + ", totalSlots="
                    + totalSlots
                    + ", isOperatingHour="
                    + isOperatingHour);
          }
          int availableSlots = totalSlots - reservedSlots;

          BigDecimal reservationRate =
              totalSlots > 0
                  ? BigDecimal.valueOf(reservedSlots * 100.0 / totalSlots)
                      .setScale(2, RoundingMode.HALF_UP)
                  : BigDecimal.ZERO;

          // 요일 문자열 생성
          String dayOfWeekStr = getDayOfWeekString(dayOfWeek);

          result.add(
              new ReservationStatisticsResponse(
                  startDate, // 기준 날짜
                  LocalTime.of(hour, 0), // 시간대
                  totalSlots,
                  reservedSlots,
                  availableSlots,
                  reservationRate,
                  "DAY_TIME_SLOT",
                  dayOfWeekStr + "_" + hour + "시", // displayKey: "MON_10시"
                  null, // staffId
                  null, // staffName
                  dayOfWeekStr // dayOfWeek
                  ));
        }
      }
    }

    System.out.println(
        "🔍 [DEBUG] getDayTimeSlotReservationStatistics 결과: " + result.size() + "개 항목");
    if (!result.isEmpty()) {
      System.out.println(
          "🔍 [DEBUG] 첫 번째 항목: "
              + result.get(0).getDisplayKey()
              + " = "
              + result.get(0).getReservationRate()
              + "%");
    }

    // 임시 테스트 데이터 추가 (실제 데이터가 없을 때)
    if (result.isEmpty() || totalReservationCount == null || totalReservationCount == 0) {
      System.out.println("🔍 [DEBUG] 실제 데이터가 없어서 테스트 데이터를 생성합니다.");
      result.clear();

      // 테스트용 샘플 데이터 생성
      String[] dayNames = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
      for (int day = 0; day < 7; day++) {
        for (int hour = 9; hour <= 20; hour++) {
          // 랜덤한 예약율 생성 (테스트용)
          double rate = Math.random() * 100;
          result.add(
              new ReservationStatisticsResponse(
                  startDate,
                  LocalTime.of(hour, 0),
                  100, // totalSlots
                  (int) rate, // reservedSlots
                  (int) (100 - rate), // availableSlots
                  BigDecimal.valueOf(rate).setScale(2, RoundingMode.HALF_UP),
                  "DAY_TIME_SLOT",
                  dayNames[day] + "_" + hour + "시",
                  null,
                  null,
                  dayNames[day]));
        }
      }
      System.out.println("🔍 [DEBUG] 테스트 데이터 " + result.size() + "개 생성 완료");
    }

    return result;
  }

  /** 요일 숫자를 문자열로 변환 (1=MON, 2=TUE, ..., 7=SUN) */
  private String getDayOfWeekString(int dayOfWeek) {
    switch (dayOfWeek) {
      case 1:
        return "MON";
      case 2:
        return "TUE";
      case 3:
        return "WED";
      case 4:
        return "THU";
      case 5:
        return "FRI";
      case 6:
        return "SAT";
      case 7:
        return "SUN";
      default:
        return "UNKNOWN";
    }
  }

  /** 30분 단위 슬롯 수 계산 */
  private int calculateTotalSlots(
      LocalTime startTime, LocalTime endTime, LocalTime lunchStart, LocalTime lunchEnd) {
    if (startTime == null || endTime == null) {
      return 0;
    }

    // 총 운영 시간 계산 (분 단위)
    int totalMinutes = (int) ChronoUnit.MINUTES.between(startTime, endTime);

    // 점심시간 제외
    if (lunchStart != null && lunchEnd != null) {
      int lunchMinutes = (int) ChronoUnit.MINUTES.between(lunchStart, lunchEnd);
      totalMinutes -= lunchMinutes;
    }

    // 30분 단위 슬롯 수 계산
    return totalMinutes / 30;
  }

  /** 기간 내 총 슬롯 수 계산 */
  private int calculateTotalSlotsForPeriod(Long shopId, LocalDate startDate, LocalDate endDate) {
    int totalSlots = 0;
    LocalDate currentDate = startDate;

    while (!currentDate.isAfter(endDate)) {
      List<Integer> availableDays =
          queryFactory
              .select(reservationSetting.id.availableDay)
              .from(reservationSetting)
              .where(
                  reservationSetting
                      .id
                      .shopId
                      .eq(shopId)
                      .and(reservationSetting.deletedAt.isNull()))
              .fetch();

      int dayOfWeek = currentDate.getDayOfWeek().getValue();

      if (availableDays.contains(dayOfWeek)) {
        var setting =
            queryFactory
                .select(
                    reservationSetting.availableStartTime,
                    reservationSetting.availableEndTime,
                    reservationSetting.lunchStartTime,
                    reservationSetting.lunchEndTime)
                .from(reservationSetting)
                .where(
                    reservationSetting
                        .id
                        .shopId
                        .eq(shopId)
                        .and(reservationSetting.id.availableDay.eq(dayOfWeek))
                        .and(reservationSetting.deletedAt.isNull()))
                .fetchOne();

        if (setting != null) {
          LocalTime startTime = setting.get(0, LocalTime.class);
          LocalTime endTime = setting.get(1, LocalTime.class);
          LocalTime lunchStart = setting.get(2, LocalTime.class);
          LocalTime lunchEnd = setting.get(3, LocalTime.class);

          totalSlots += calculateTotalSlots(startTime, endTime, lunchStart, lunchEnd);
        }
      }

      currentDate = currentDate.plusDays(1);
    }

    return totalSlots;
  }

  @Override
  public List<HourlyVisitorStatisticsResponse> findHourlyVisitorStatistics(
      Long shopId, LocalDate startDate, LocalDate endDate) {

    List<HourlyVisitorStatisticsResponse> result = new ArrayList<>();

    // 시간대별 방문객 수를 성별로 구분하여 조회
    for (int hour = 9; hour <= 20; hour++) {
      // 남성 방문객 수 조회
      Long maleCount =
          queryFactory
              .select(reservation.count())
              .from(reservation)
              .leftJoin(customer)
              .on(reservation.customerId.eq(customer.id))
              .where(
                  reservation
                      .shopId
                      .eq(shopId)
                      .and(
                          reservation.reservationStartAt.between(
                              startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                      .and(reservation.reservationStartAt.hour().eq(hour))
                      .and(customer.gender.stringValue().eq("M"))
                      .and(reservation.deletedAt.isNull())
                      .and(
                          reservation.reservationStatusName.notIn(
                              ReservationStatusName.CBC, ReservationStatusName.CBS)))
              .fetchOne();

      // 여성 방문객 수 조회
      Long femaleCount =
          queryFactory
              .select(reservation.count())
              .from(reservation)
              .leftJoin(customer)
              .on(reservation.customerId.eq(customer.id))
              .where(
                  reservation
                      .shopId
                      .eq(shopId)
                      .and(
                          reservation.reservationStartAt.between(
                              startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                      .and(reservation.reservationStartAt.hour().eq(hour))
                      .and(customer.gender.stringValue().eq("F"))
                      .and(reservation.deletedAt.isNull())
                      .and(
                          reservation.reservationStatusName.notIn(
                              ReservationStatusName.CBC, ReservationStatusName.CBS)))
              .fetchOne();

      long maleVisitors = maleCount != null ? maleCount : 0;
      long femaleVisitors = femaleCount != null ? femaleCount : 0;
      long totalVisitors = maleVisitors + femaleVisitors;

      String timeSlot = String.format("%02d:00", hour);
      String displayTime = hour + "시";

      result.add(
          new HourlyVisitorStatisticsResponse(
              hour, timeSlot, maleVisitors, femaleVisitors, totalVisitors, displayTime));
    }

    return result;
  }

  @Override
  public List<DailyVisitorStatisticsResponse> findDailyVisitorStatistics(
      Long shopId, LocalDate startDate, LocalDate endDate) {

    List<DailyVisitorStatisticsResponse> result = new ArrayList<>();

    LocalDate currentDate = startDate;
    while (!currentDate.isAfter(endDate)) {
      // 남성 방문객 수 조회
      Long maleCount =
          queryFactory
              .select(reservation.count())
              .from(reservation)
              .leftJoin(customer)
              .on(reservation.customerId.eq(customer.id))
              .where(
                  reservation
                      .shopId
                      .eq(shopId)
                      .and(
                          reservation.reservationStartAt.between(
                              currentDate.atStartOfDay(), currentDate.atTime(LocalTime.MAX)))
                      .and(customer.gender.stringValue().eq("M"))
                      .and(reservation.deletedAt.isNull())
                      .and(
                          reservation.reservationStatusName.notIn(
                              ReservationStatusName.CBC, ReservationStatusName.CBS)))
              .fetchOne();

      // 여성 방문객 수 조회
      Long femaleCount =
          queryFactory
              .select(reservation.count())
              .from(reservation)
              .leftJoin(customer)
              .on(reservation.customerId.eq(customer.id))
              .where(
                  reservation
                      .shopId
                      .eq(shopId)
                      .and(
                          reservation.reservationStartAt.between(
                              currentDate.atStartOfDay(), currentDate.atTime(LocalTime.MAX)))
                      .and(customer.gender.stringValue().eq("F"))
                      .and(reservation.deletedAt.isNull())
                      .and(
                          reservation.reservationStatusName.notIn(
                              ReservationStatusName.CBC, ReservationStatusName.CBS)))
              .fetchOne();

      long maleVisitors = maleCount != null ? maleCount : 0;
      long femaleVisitors = femaleCount != null ? femaleCount : 0;
      long totalVisitors = maleVisitors + femaleVisitors;

      String dayOfWeek = getDayOfWeekKorean(currentDate);
      String displayDate =
          String.format("%02d/%02d", currentDate.getMonthValue(), currentDate.getDayOfMonth());

      result.add(
          new DailyVisitorStatisticsResponse(
              currentDate, dayOfWeek, maleVisitors, femaleVisitors, totalVisitors, displayDate));

      currentDate = currentDate.plusDays(1);
    }

    return result;
  }

  private String getDayOfWeekKorean(LocalDate date) {
    switch (date.getDayOfWeek()) {
      case MONDAY:
        return "월";
      case TUESDAY:
        return "화";
      case WEDNESDAY:
        return "수";
      case THURSDAY:
        return "목";
      case FRIDAY:
        return "금";
      case SATURDAY:
        return "토";
      case SUNDAY:
        return "일";
      default:
        return "";
    }
  }

  @Override
  public List<AdvancedSalesStatisticsResponse> findPrimaryItemDailyTrend(
      Long shopId, LocalDate startDate, LocalDate endDate) {

    // 1차 상품별 일별 매출추이를 조회
    List<AdvancedSalesStatisticsResponse> results =
        queryFactory
            .select(
                sales.salesDate.stringValue(),
                primaryItem.primaryItemName,
                itemSales.quantity.multiply(secondaryItem.secondaryItemPrice).sum().coalesce(0),
                sales.salesId.countDistinct().coalesce(0L),
                Expressions.numberTemplate(
                    Integer.class,
                    "COALESCE(SUM(CASE WHEN {0} > 0 THEN "
                        + "CAST(({1} * {2} * {3}) / {0} AS INTEGER) ELSE 0 END), 0)",
                    sales.totalAmount,
                    itemSales.quantity,
                    secondaryItem.secondaryItemPrice,
                    sales.discountAmount),
                Expressions.numberTemplate(
                    Integer.class,
                    "COALESCE(SUM(CASE WHEN {0} > 0 AND {4} IS NOT NULL THEN "
                        + "CAST(({1} * {2} * {3}) / {0} AS INTEGER) ELSE 0 END), 0)",
                    sales.totalAmount,
                    itemSales.quantity,
                    secondaryItem.secondaryItemPrice,
                    sales.discountAmount,
                    itemSales.couponId))
            .from(sales)
            .leftJoin(itemSales)
            .on(sales.salesId.eq(itemSales.salesId))
            .leftJoin(secondaryItem)
            .on(itemSales.secondaryItemId.eq(secondaryItem.secondaryItemId))
            .leftJoin(primaryItem)
            .on(secondaryItem.primaryItemId.eq(primaryItem.primaryItemId))
            .where(
                sales
                    .shopId
                    .eq(shopId)
                    .and(
                        sales.salesDate.between(
                            startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX)))
                    .and(sales.isRefunded.isFalse())
                    .and(itemSales.salesId.isNotNull())
                    .and(secondaryItem.secondaryItemId.isNotNull())
                    .and(primaryItem.primaryItemId.isNotNull())
                    .and(sales.deletedAt.isNull())
                    .and(itemSales.deletedAt.isNull())
                    .and(secondaryItem.deletedAt.isNull())
                    .and(primaryItem.deletedAt.isNull()))
            .groupBy(sales.salesDate, primaryItem.primaryItemName)
            .orderBy(sales.salesDate.asc(), primaryItem.primaryItemName.asc())
            .fetch()
            .stream()
            .map(
                tuple -> {
                  String date = tuple.get(0, String.class);
                  String primaryItemName = tuple.get(1, String.class);

                  // 날짜 형식 변환 (필요시)
                  String formattedDate = date;
                  if (date != null && date.contains("T")) {
                    formattedDate = date.split("T")[0];
                  }

                  return AdvancedSalesStatisticsResponse.builder()
                      .date(formattedDate)
                      .gender(null)
                      .category(null)
                      .primaryItemName(primaryItemName)
                      .secondaryItemName(null)
                      .totalSalesAmount(tuple.get(2, Integer.class).longValue())
                      .totalTransactions(tuple.get(3, Long.class))
                      .totalDiscountAmount(tuple.get(4, Integer.class).longValue())
                      .totalCouponDiscountAmount(tuple.get(5, Integer.class).longValue())
                      .build();
                })
            .collect(Collectors.toList());

    return results;
  }
}
