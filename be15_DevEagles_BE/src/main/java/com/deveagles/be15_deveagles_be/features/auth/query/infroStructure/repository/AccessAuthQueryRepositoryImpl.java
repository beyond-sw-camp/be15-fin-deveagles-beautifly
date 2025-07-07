package com.deveagles.be15_deveagles_be.features.auth.query.infroStructure.repository;

import com.deveagles.be15_deveagles_be.features.auth.command.domain.aggregate.QAccessAuth;
import com.deveagles.be15_deveagles_be.features.auth.command.domain.aggregate.QAccessList;
import com.deveagles.be15_deveagles_be.features.users.command.application.dto.response.StaffPermissions;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AccessAuthQueryRepositoryImpl implements AccessAuthQueryRepository {

  private final JPAQueryFactory queryFactory;

  @Override
  public List<StaffPermissions> getAccessPermissionsByStaffId(Long staffId) {

    QAccessAuth accessAuth = QAccessAuth.accessAuth;
    QAccessList accessList = QAccessList.accessList;

    return queryFactory
        .select(
            Projections.constructor(
                StaffPermissions.class,
                accessAuth.accessId,
                accessList.accessName,
                accessAuth.canRead,
                accessAuth.canWrite,
                accessAuth.canDelete,
                accessAuth.isActive))
        .from(accessAuth)
        .join(accessList)
        .on(accessAuth.accessId.eq(accessList.accessId))
        .where(accessAuth.staffId.eq(staffId))
        .fetch();
  }
}
