package com.deveagles.be15_deveagles_be.features.items.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.PrimaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.repository.PrimaryItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaPrimaryItemRepository
    extends JpaRepository<PrimaryItem, Long>, PrimaryItemRepository {}
