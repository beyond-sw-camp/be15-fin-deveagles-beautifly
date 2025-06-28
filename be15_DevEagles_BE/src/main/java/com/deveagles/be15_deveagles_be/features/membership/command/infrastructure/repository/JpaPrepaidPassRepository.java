package com.deveagles.be15_deveagles_be.features.membership.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.membership.command.domain.aggregate.PrepaidPass;
import com.deveagles.be15_deveagles_be.features.membership.command.domain.repository.PrepaidPassRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaPrepaidPassRepository
    extends JpaRepository<PrepaidPass, Long>, PrepaidPassRepository {}
