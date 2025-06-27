package com.deveagles.be15_deveagles_be.features.membership.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.SessionPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.SessionPassRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaSessionPassRepository
    extends JpaRepository<SessionPass, Long>, SessionPassRepository {}
