package com.deveagles.be15_deveagles_be.features.users.command.repository;

import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<Staff, Long> {

  Optional<Staff> findStaffByLoginId(String loginId0);

  Optional<Staff> findStaffByEmail(String email);

  Optional<Staff> findStaffByStaffId(Long aLong);

  @Query("SELECT s FROM Staff s WHERE s.staffName = :staffName AND s.email = :email")
  Optional<Staff> findStaffForGetPwd(String staffName, String email);

  List<Staff> findAllByShopId(Long shopId);

  Page<Staff> findByShopId(Long shopId, Pageable pageable);

  List<Staff> findByShopIdAndLeftDateIsNull(Long shopId);
}
