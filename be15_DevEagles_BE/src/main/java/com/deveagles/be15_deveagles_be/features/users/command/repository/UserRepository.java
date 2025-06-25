package com.deveagles.be15_deveagles_be.features.users.command.repository;

import com.deveagles.be15_deveagles_be.features.users.command.domain.aggregate.Staff;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<Staff, Long> {

  Optional<Staff> findStaffByLoginId(String loginId0);

  Optional<Staff> findStaffByEmail(String email);
}
