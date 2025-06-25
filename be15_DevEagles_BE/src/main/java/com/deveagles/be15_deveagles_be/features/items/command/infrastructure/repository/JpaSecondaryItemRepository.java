package com.deveagles.be15_deveagles_be.features.items.command.infrastructure.repository;

import com.deveagles.be15_deveagles_be.features.items.command.domain.aggregate.SecondaryItem;
import com.deveagles.be15_deveagles_be.features.items.command.domain.repository.SecondaryItemRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaSecondaryItemRepository
    extends JpaRepository<SecondaryItem, Long>, SecondaryItemRepository {}
