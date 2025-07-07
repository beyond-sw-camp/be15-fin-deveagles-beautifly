package com.deveagles.be15_deveagles_be.features.auth.command.repository;

import com.deveagles.be15_deveagles_be.features.auth.command.domain.aggregate.AccessAuth;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessAuthRepository extends JpaRepository<AccessAuth, Long> {}
