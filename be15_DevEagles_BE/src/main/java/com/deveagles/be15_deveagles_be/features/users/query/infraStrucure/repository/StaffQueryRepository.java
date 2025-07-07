package com.deveagles.be15_deveagles_be.features.users.query.infraStrucure.repository;

import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StaffQueryRepository {

  Page<Staff> searchStaffs(Long shopId, String keyword, Boolean isActive, Pageable pageable);
}
