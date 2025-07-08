package com.deveagles.be15_deveagles_be.features.auth.query.infroStructure.repository;

import com.deveagles.be15_deveagles_be.features.users.command.application.dto.response.StaffPermissions;
import java.util.List;

public interface AccessAuthQueryRepository {
  List<StaffPermissions> getAccessPermissionsByStaffId(Long staffId);
}
