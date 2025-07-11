package com.deveagles.be15_deveagles_be.features.users.query.infraStrucure.repository;

import static com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.QStaff.staff;

import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class StaffQueryRepositoryImpl implements StaffQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public Page<Staff> searchStaffs(
      Long shopId, String keyword, Boolean isActive, Pageable pageable) {
    List<Staff> content =
        queryFactory
            .selectFrom(staff)
            .where(staff.shopId.eq(shopId), keywordContains(keyword), isActiveEq(isActive))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(staff.staffId.desc())
            .fetch();

    // count
    long total =
        queryFactory
            .select(staff.count())
            .from(staff)
            .where(staff.shopId.eq(shopId), keywordContains(keyword), isActiveEq(isActive))
            .fetchOne();

    return new PageImpl<>(content, pageable, total);
  }

  private BooleanExpression keywordContains(String keyword) {
    if (keyword == null || keyword.isBlank()) return null;
    return staff.staffName.containsIgnoreCase(keyword);
  }

  private BooleanExpression isActiveEq(Boolean isActive) {
    if (isActive == null || !isActive) return null;
    return staff.leftDate.isNull();
  }
}
