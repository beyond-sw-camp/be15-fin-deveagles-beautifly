package com.deveagles.be15_deveagles_be.features.auth.command.repository;

import com.deveagles.be15_deveagles_be.features.auth.command.domain.aggregate.AccessList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessListRepository extends JpaRepository<AccessList, Long> {}
